package alfredabdo.ide.plugins.translations.exportToExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.settings.ui.defaultTranslationsExportDirectory
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
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
    ) {
        class Language(
            val label: String,
            val code: String,
        )
    }

    fun export(
        file: XmlFile,
        details: Details,
    ) {
        scope.launch(Dispatchers.IO) {
            val root = file.rootTag
            if (root?.name != "resources") {
                notificationGroup.createNotification(
                    TranslationsHelperBundle.message("service.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelService.error.noResources"),
                    NotificationType.ERROR,
                ).notify(project)
                return@launch
            }
            val elements = root.subTags.takeUnless { it.isEmpty() } ?: run {
                notificationGroup.createNotification(
                    TranslationsHelperBundle.message("service.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelService.error.noElements"),
                    NotificationType.ERROR,
                ).notify(project)
                return@launch
            }


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

            exportXMLToExcel(elements, outputFile)

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


    private fun exportXMLToExcel(elements: Array<XmlTag>, outputFile: File) {
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
            createCell(1, CellType.STRING).run {
                setCellValue("English")
                cellStyle = headerStyle
            }
        }
        rowIndex++

        runReadAction {
            elements.asSequence()
                .filter { it.name == "string" }
                .forEach { element ->
                    element.getAttributeValue("name")?.let { name ->
                        val value = element.value.text
                        sheet.createRow(rowIndex).run {
                            createCell(0, CellType.STRING).run {
                                setCellValue(name)
                            }
                            createCell(1, CellType.STRING).run {
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


    private val notificationGroup
        get() = NotificationGroupManager.getInstance()
            .getNotificationGroup("alfredabdo.ide.plugins.translations.notifications.group.ExportStringsToExcel")
}