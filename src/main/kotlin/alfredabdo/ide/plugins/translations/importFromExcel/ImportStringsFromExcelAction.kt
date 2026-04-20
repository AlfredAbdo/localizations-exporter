@file:Suppress("UnstableApiUsage")

package alfredabdo.ide.plugins.translations.importFromExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.asXMLFile
import alfredabdo.ide.plugins.translations.getLanguageCode
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
import com.intellij.openapi.vfs.refreshAndFindVirtualFile
import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.tools.projectWizard.core.asPath

class ImportStringsFromExcelAction : AnAction() {

    override fun update(e: AnActionEvent) {
        val file = e.getData(LangDataKeys.PSI_FILE)?.asXMLFile()
        e.presentation.isVisible = file != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(p0: AnActionEvent) {
        val file = p0.getData(LangDataKeys.PSI_FILE)?.asXMLFile() ?: return

        val details = p0.project?.awaitInfo(file)
        details?.let {
            p0.project?.service<ImportStringsFromExcelService>()
                ?.import(file, it)
        }
    }


    private fun Project.awaitInfo(file: XmlFile): ImportStringsFromExcelService.Details? {
        val containingDirectoryLanguageCode = file.getLanguageCode()
        val info = ImportStringsFromExcelActionInfo(
            mutableStateListOf(
                ImportLanguageItemData.forCurrentFile(null, containingDirectoryLanguageCode),
            ),
        )

        val dialog = ComposeDialogWrapper(
            TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.title"),
            this,
            onValidate = {
                if (info.filePath.isEmpty()) {
                    info.showFilePathError = true
                    ValidationInfo(TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.chooseFile.missing"))
                } else if (info.languages.any { it.code.isBlank() }) {
                    ValidationInfo(TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.languages.emptyCode"))
                } else {
                    null
                }
            },
        ) {
            ImportStringsFromExcelActionPanel(info)
        }

        return if (dialog.showAndGet())
            ImportStringsFromExcelService.Details(
                info.filePath.asPath().refreshAndFindVirtualFile()!!.toIoFile(),
                info.idColumnIndex,
                info.languages.map { language ->
                    ImportStringsFromExcelService.Details.Language(language.columnIndex, language.code, language.isCurrentFile)
                },
                info.shouldOverwriteResources,
            )
        else
            null
    }
}