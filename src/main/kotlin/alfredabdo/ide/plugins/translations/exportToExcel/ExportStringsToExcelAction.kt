package alfredabdo.ide.plugins.translations.exportToExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.asXMLFile
import alfredabdo.ide.plugins.translations.getLanguageCode
import alfredabdo.ide.plugins.translations.settings.resolveExportDirectoryPath
import alfredabdo.ide.plugins.translations.ui.common.ComposeDialogWrapper
import androidx.compose.runtime.mutableStateListOf
import com.android.tools.idea.util.toIoFile
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.refreshAndFindVirtualDirectory
import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.tools.projectWizard.core.asPath

class ExportStringsToExcelAction : AnAction() {

    override fun update(e: AnActionEvent) {
        val file = e.getData(LangDataKeys.PSI_FILE)?.asXMLFile()
        e.presentation.isVisible = file != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(p0: AnActionEvent) {
        val file = p0.getData(LangDataKeys.PSI_FILE)?.asXMLFile() ?: return

        val details = p0.project?.awaitInfo(file)
        details?.let {
            p0.project?.service<ExportStringsToExcelService>()
                ?.export(file, it)
        }
    }


    private fun Project.awaitInfo(file: XmlFile): ExportStringsToExcelService.Details? {
        val containingDirectoryLanguageCode = file.getLanguageCode()
        val info = ExportStringsToExcelActionInfo(
            directoryPath = resolveExportDirectoryPath().orEmpty(),
            languages = mutableStateListOf(
                ExportLanguageItemData.forCurrentFile(null, containingDirectoryLanguageCode),
            ),
            onlyIfMissing = false,
            advancedOptions = ExportStringsToExcelActionInfo.AdvancedOptions(
                ampersandConversion = false,
                cdataUnwrapping = false,
            ),
        )

        val dialog = ComposeDialogWrapper(
            TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.title"),
            this,
            onValidate = {
                if (info.languages.any { it.code.isBlank() }) {
                    ValidationInfo(TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.languages.emptyCode"))
                } else {
                    null
                }
            },
        ) {
            ExportStringsToExcelActionPanel(
                file.name,
                this.name,
                info,
            )
        }

        return if (dialog.showAndGet())
            ExportStringsToExcelService.Details(
                if (info.directoryPath.isNotEmpty()) {
                    info.directoryPath.asPath().refreshAndFindVirtualDirectory()!!.toIoFile()
                } else {
                    null
                },
                info.languages.map { language ->
                    ExportStringsToExcelService.Details.Language(language.label, language.code, language.isCurrentFile)
                },
                info.onlyIfMissing,
                info.advancedOptions.let { advanced ->
                    ExportStringsToExcelService.Details.Advanced(
                        advanced.ampersandConversion,
                        advanced.cdataUnwrapping,
                    )
                },
            )
        else
            null
    }
}