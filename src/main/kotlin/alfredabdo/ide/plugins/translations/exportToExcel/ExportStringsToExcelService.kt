package alfredabdo.ide.plugins.translations.exportToExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.getTranslatedFile
import alfredabdo.ide.plugins.translations.settings.ui.defaultTranslationsExportDirectory
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.readAction
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.xml.XmlFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.awt.Desktop
import java.io.File

@Service(Service.Level.PROJECT)
class ExportStringsToExcelService(
    private val project: Project,
    private val scope: CoroutineScope,
) {

    class Details(
        val directory: File?,
        val languages: List<Language>,
        val onlyIfMissing: Boolean,
        val advanced: Advanced,
    ) {
        class Language(
            val label: String,
            val code: String,
            val isCurrentFile: Boolean,
        )

        class Advanced(
            val ampersandConversion: Boolean,
            val cdataUnwrapping: Boolean,
        )
    }

    fun export(
        file: XmlFile,
        details: Details,
    ) {
        scope.launch(Dispatchers.IO) {
            val outputFileName = "${file.name.removeSuffix(".xml")}.xlsx"
            val outputDirectory = details.directory
                ?.takeIf { it.isDirectory }
                ?: File(defaultTranslationsExportDirectory)
            val outputFile = File(
                outputDirectory,
                listOf(
                    project.name,
                    outputFileName,
                ).joinToString(File.separator)
            )
            if (!outputFile.parentFile.exists()) {
                outputFile.parentFile.mkdirs()
            }

            exportXMLToExcel(
                file,
                outputFile,
                details.languages,
                details.onlyIfMissing,
                details.advanced.ampersandConversion,
                details.advanced.cdataUnwrapping,
            )

            val notification = notificationGroup.createNotification(
                TranslationsHelperBundle.message(
                    "service.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelService.success.message",
                    project.name,
                ),
                NotificationType.INFORMATION,
            )
            notification.addAction(
                object : AnAction(TranslationsHelperBundle.lazyMessage("alfredabdo.ide.plugins.translations.general.showFile")) {
                    override fun actionPerformed(p0: AnActionEvent) {
                        val desktop = Desktop.getDesktop()
                        if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            desktop.browse(outputFile.parentFile.toURI())
                        }

                        notification.hideBalloon()
                    }
                }
            )
            notification.notify(project)
        }
    }


    private suspend fun exportXMLToExcel(
        currentFile: XmlFile,
        outputFile: File,
        languages: List<Details.Language>,
        onlyIfMissing: Boolean,
        ampersandConversion: Boolean,
        cdataUnwrapping: Boolean,
    ) {
        val defaultCode = TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.code.default")
        val data = languages
            .map { language ->
                CodeInfo(
                    language.code,
                    language.label,
                    if (language.isCurrentFile) {
                        currentFile
                    } else {
                        currentFile.getTranslatedFile(language.code.takeUnless { it == defaultCode }, currentFile.name)
                    },
                )
            }


        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Strings")
        var rowIndex = 0

        val headerStyle = workbook.createCellStyle().apply {
            setFont(workbook.createFont().apply { bold = true })
        }
        sheet.createRow(rowIndex).run {
            createCell(0, CellType.STRING).run {
                setCellValue("ID")
                cellStyle = headerStyle
            }
            data.forEachIndexed { index, info ->
                createCell(1 + index, CellType.STRING).run {
                    setCellValue(info.label)
                    cellStyle = headerStyle
                }
            }
        }
        rowIndex++


        /*runReadAction {
            val elements = data.asSequence()
                .map { info ->
                    info to (info.file?.rootTag?.takeIf { it.name == "resources" }?.subTags ?: emptyArray())
                }
                .flatMap { (info, elements) ->
                    elements.asSequence()
                        .filter { element -> element.name == "string" && element.getAttributeValue("name") != null }
                        .map { element -> element to info.code }
                        .toList()
                }
                .groupBy { (element, *//*code*//*_) -> element.getAttributeValue("name").orEmpty() }

            elements.forEach { (id, relatedElements) ->
                if (!onlyIfMissing || relatedElements.size < languages.size) {
                    sheet.createRow(rowIndex).run {
                        createCell(0, CellType.STRING).run {
                            setCellValue(id)
                        }

                        relatedElements.forEach { (element, code) ->
                            val value = element.value.text
                                .let { text -> if (ampersandConversion) text.replace("&amp;", "&") else text }
                                .let { text -> if (cdataUnwrapping) text.removeSurrounding("<![CDATA[", "]]>") else text }
                            val index = languages.indexOfFirst { it.code == code }
                            createCell(1 + index, CellType.STRING).run {
                                setCellValue(value)
                            }
                        }
                    }
                    rowIndex++
                }
            }
        }*/

        val elements = readAction {
            data.asSequence()
                .map { info ->
                    info to (info.file?.rootTag?.takeIf { it.name == "resources" }?.subTags ?: emptyArray())
                }
                .flatMap { (info, elements) ->
                    elements.asSequence()
                        .filter { element -> element.name == "string" && element.getAttributeValue("name") != null }
                        .map { element -> element to info.code }
                        .toList()
                }
                .groupBy { (element, /*code*/_) -> element.getAttributeValue("name").orEmpty() }
        }
        elements.forEach { (id, relatedElements) ->
            if (!onlyIfMissing || relatedElements.size < languages.size) {
                sheet.createRow(rowIndex).run {
                    createCell(0, CellType.STRING).run {
                        setCellValue(id)
                    }

                    relatedElements.forEach { (element, code) ->
                        val value = element.value.text
                            .let { text -> if (ampersandConversion) text.replace("&amp;", "&") else text }
                            .let { text -> if (cdataUnwrapping) text.removeSurrounding("<![CDATA[", "]]>") else text }
                        val index = languages.indexOfFirst { it.code == code }
                        createCell(1 + index, CellType.STRING).run {
                            setCellValue(value)
                        }
                    }
                }
                rowIndex++
            }
        }

        outputFile.outputStream().use { fos ->
            workbook.write(fos)
        }

        workbook.close()
    }

    private class CodeInfo(
        val code: String,
        val label: String,
        val file: XmlFile?,
    )


    private val notificationGroup
        get() = NotificationGroupManager.getInstance()
            .getNotificationGroup("alfredabdo.ide.plugins.translations.notifications.group.ExportStringsToExcel")
}