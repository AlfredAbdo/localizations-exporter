package alfredabdo.ide.plugins.translations.importFromExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.getTranslatedFile
import alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelService.Details.Language
import alfredabdo.ide.plugins.translations.importFromExcel.exception.InvalidResourcesFileException
import alfredabdo.ide.plugins.translations.utils.runWriteCommandAction
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.EDT
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findDirectory
import com.intellij.openapi.vfs.findFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File


@Service(Service.Level.PROJECT)
class ImportStringsFromExcelService(
    private val project: Project,
    private val scope: CoroutineScope,
) {

    class Details(
        val xlsxFile: File,
        val idColumnIndex: Int,
        val languages: List<Language>,
        val shouldOverwriteResources: Boolean,
    ) {
        class Language(
            val columnIndex: Int,
            val code: String,
            val isCurrentFile: Boolean,
        )
    }


    fun import(
        file: XmlFile,
        details: Details,
    ) {
        scope.launch(Dispatchers.EDT) {
            try {
                val files = importXMLFromExcel(
                    file,
                    details.xlsxFile,
                    details.idColumnIndex,
                    details.languages,
                    details.shouldOverwriteResources,
                )

                val notification = notificationGroup.createNotification(
                    TranslationsHelperBundle.message(
                        "service.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelService.success.message",
                        details.xlsxFile.name,
                        project.name,
                    ),
                    NotificationType.INFORMATION,
                )
                notification.addAction(
                    object : AnAction(TranslationsHelperBundle.lazyMessage("alfredabdo.ide.plugins.translations.general.showFiles")) {
                        override fun actionPerformed(p0: AnActionEvent) {
                            files.forEachIndexed { index, file ->
                                FileEditorManager.getInstance(project)
                                    .openFile(file, index == 0)
                            }

                            //ProjectView.getInstance(project).selectPsiElement(destinationFile, true)

                            notification.hideBalloon()
                        }
                    }
                )
                notification.notify(project)
            } catch (e: InvalidResourcesFileException) {
                notificationGroup.createNotification(
                    e.message.orEmpty(),
                    NotificationType.ERROR,
                ).notify(project)
            }
        }
    }


    @Throws(InvalidResourcesFileException::class)
    private suspend fun importXMLFromExcel(
        currentFile: XmlFile,
        inputFile: File,
        idColumnIndex: Int,
        languages: List<Language>,
        shouldOverwriteResources: Boolean,
    ): List<VirtualFile> {
        val fis = inputFile.inputStream()
        val workbook = XSSFWorkbook(fis)
        val sheet = workbook.getSheetAt(0)

        val defaultCode = TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.code.default")
        val data = languages
            .map { language ->
                val xmlFile = if (language.isCurrentFile) {
                    currentFile
                } else {
                    currentFile.getTranslatedFile(language.code.takeUnless { it == defaultCode }, inputFile.name)
                }

                val destinationFile = xmlFile?.virtualFile
                    ?: currentFile.createFileForLanguageCode(language.code, currentFile.name)!! //fixme need better handling

                CodeInfo(
                    language.columnIndex,
                    xmlFile
                        ?: fileFactory.createFileFromText(
                            currentFile.name,
                            XmlFileType.INSTANCE,
                            "<resources></resources>"
                        ) as XmlFile,
                    xmlFile != null,
                    destinationFile,
                )
            }


        WriteCommandAction.runWriteCommandAction(project) {
            if (data.any { info -> info.xmlFile.rootTag?.name != "resources" }) {
                workbook.close()
                throw InvalidResourcesFileException()
            }


            sheet.forEach { row ->
                if (row.rowNum == 0) {
                    //skip
                } else {
                    val id = row.getCell(idColumnIndex)?.stringCellValue?.trim()
                    val valuesMap = data.map { info ->
                        info.xmlFile.rootTag!! to row.getCell(info.columnIndex)?.stringCellValue?.trim()
                    }

                    if (!shouldOverwriteResources) {
                        valuesMap.forEach { (root, value) -> root.addStringChild(id, value) }
                    } else {
                        valuesMap.forEach { (root, value) ->
                            root.findSubTags("string").firstOrNull { subTag -> subTag.getAttributeValue("name") == id }
                                ?.let { subTag -> subTag.value.text = value.orEmpty() }
                                ?: root.addStringChild(id, value)
                        }
                    }
                }
            }

            val codeStyleManager = CodeStyleManager.getInstance(project)
            data.forEach { info ->
                try {
                    codeStyleManager.reformat(info.xmlFile)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (!info.originalXmlFileAvailable) {
                    info.psiFile.setBinaryContent(info.xmlFile.text.toByteArray())
                }
            }
        }

        workbook.close()

        return data.map { it.psiFile }
    }

    private suspend fun XmlFile.createFileForLanguageCode(code: String, targetFileName: String): VirtualFile? {
        return runWriteCommandAction(this@ImportStringsFromExcelService.project) {
            virtualFile.parent?.parent
                ?.let {
                    it.findDirectory("values-$code") ?: it.createChildDirectory(project, "values-$code")
                }
                ?.let {
                    it.findFile(targetFileName) ?: it.createChildData(project, targetFileName)
                }
        }
    }

    private fun XmlTag.addStringChild(name: String?, value: String?) {
        val childTag = createChildTag(
            "string",
            null,
            value,
            false,
        ).apply {
            setAttribute("name", name)
        }
        add(childTag)
    }

    private class CodeInfo(
        val columnIndex: Int,
        val xmlFile: XmlFile,
        val originalXmlFileAvailable: Boolean,
        val psiFile: VirtualFile,
    )


    private val fileFactory = PsiFileFactory.getInstance(project)

    private val notificationGroup
        get() = NotificationGroupManager.getInstance()
            .getNotificationGroup("alfredabdo.ide.plugins.translations.notifications.group.ImportStringsFromExcel")
}