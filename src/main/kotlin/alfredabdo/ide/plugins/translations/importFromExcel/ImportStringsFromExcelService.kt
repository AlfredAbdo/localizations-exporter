package alfredabdo.ide.plugins.translations.importFromExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.getTranslatedFile
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
        val translatedLanguageCode: String,
        val translatedColumnIndex: Int,
    )


    fun import(
        targetFile: XmlFile,
        details: Details,
    ) {
        scope.launch(Dispatchers.EDT) {
            val availableXmlFile = if (details.translatedLanguageCode.isEmpty())
                targetFile
            else
                targetFile.getTranslatedFile(details.translatedLanguageCode, targetFile.name)

            val destinationFile = availableXmlFile?.virtualFile
                ?: targetFile.createFileForLanguageCode(details.translatedLanguageCode, targetFile.name)
                ?: return@launch

            val targetXmlFile = availableXmlFile
                ?: fileFactory.createFileFromText(
                    targetFile.name,
                    XmlFileType.INSTANCE,
                    "<resources></resources>"
                ) as? XmlFile
                ?: return@launch

            val root = targetXmlFile.rootTag
            if (root?.name != "resources") {
                notificationGroup.createNotification(
                    TranslationsHelperBundle.message("service.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelService.error.invalidDestination"),
                    NotificationType.ERROR,
                ).notify(project)
                return@launch
            }

            importXMLFromExcel(details, availableXmlFile, targetXmlFile, root, destinationFile)

            val notification = notificationGroup.createNotification(
                TranslationsHelperBundle.message(
                    "service.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelService.success.message",
                    details.xlsxFile.name,
                    project.name,
                ),
                NotificationType.INFORMATION,
            )
            notification.addAction(
                object : AnAction(TranslationsHelperBundle.lazyMessage("alfredabdo.ide.plugins.translations.general.showFile")) {
                    override fun actionPerformed(p0: AnActionEvent) {
                        FileEditorManager.getInstance(project)
                            .openFile(destinationFile, true)

                        //ProjectView.getInstance(project).selectPsiElement(destinationFile, true)

                        notification.hideBalloon()
                    }
                }
            )
            notification.notify(project)
        }
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

    private fun importXMLFromExcel(
        inputDetails: Details,
        availableXmlFile: XmlFile?,
        targetXmlFile: XmlFile,
        targetRoot: XmlTag,
        outputFile: VirtualFile,
    ) {
        val fis = inputDetails.xlsxFile.inputStream()
        val workbook = XSSFWorkbook(fis)
        val sheet = workbook.getSheetAt(0)

        WriteCommandAction.runWriteCommandAction(project) {
            sheet.forEach {
                if (it.rowNum == 0) {
                    //skip
                } else {
                    val childTag = targetRoot.createChildTag(
                        "string",
                        null,
                        it.getCell(inputDetails.translatedColumnIndex)?.stringCellValue?.trim(),
                        false,
                    )
                    childTag.setAttribute("name", it.getCell(inputDetails.idColumnIndex)?.stringCellValue?.trim())
                    targetRoot.add(childTag)
                }
            }

            try {
                CodeStyleManager.getInstance(project).reformat(targetXmlFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (availableXmlFile == null) {
                outputFile.setBinaryContent(targetXmlFile.text.toByteArray())
            }
        }
    }


    private val fileFactory = PsiFileFactory.getInstance(project)

    private val notificationGroup
        get() = NotificationGroupManager.getInstance()
            .getNotificationGroup("alfredabdo.ide.plugins.translations.notifications.group.ImportStringsFromExcel")
}