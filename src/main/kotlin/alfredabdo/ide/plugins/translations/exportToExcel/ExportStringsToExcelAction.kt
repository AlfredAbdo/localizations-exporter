package alfredabdo.ide.plugins.translations.exportToExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.asXMLFile
import alfredabdo.ide.plugins.translations.settings.resolveExportDirectoryPath
import alfredabdo.ide.plugins.translations.settings.ui.defaultTranslationsExportDirectory
import alfredabdo.ide.plugins.translations.ui.LanguageItemData
import alfredabdo.ide.plugins.translations.ui.LanguagesComponent
import alfredabdo.ide.plugins.translations.ui.common.ComposeDialogWrapper
import alfredabdo.ide.plugins.translations.ui.common.TextFieldWithBrowseButtonAndContextHelp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.tools.idea.util.toIoFile
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.refreshAndFindVirtualDirectory
import com.intellij.psi.xml.XmlFile
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.kotlin.tools.projectWizard.core.asPath
import java.io.File

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
        val info = Info(
            directoryPath = resolveExportDirectoryPath().orEmpty(),
            onlyIfMissing = false,
        )

        val dialog = ComposeDialogWrapper(
            TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.title"),
            this,
        ) {
            Panel(
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
                    ExportStringsToExcelService.Details.Language(language.label, language.code)
                },
                info.onlyIfMissing,
            )
        else
            null
    }


    @Composable
    private fun Panel(
        fileName: String,
        projectName: String,
        info: Info,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                TranslationsHelperBundle.message(
                    "action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.confirmation.message",
                    fileName,
                ),
            )

            LanguagesComponent(
                info.languages,
                onlyIfMissing = info.onlyIfMissing,
                onOnlyIfMissingChanged = { info.onlyIfMissing = it },
                onAddLanguage = {
                    info.languages += LanguageItemData("", "")
                },
                Modifier.fillMaxWidth(),
            )

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val filePathTextState = rememberTextFieldState()
                val descriptor = remember {
                    FileChooserDescriptorFactory.createSingleFolderDescriptor().apply {
                        title =
                            TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.chooseDirectory.title")
                        withFileFilter { file -> file.isDirectory }
                    }
                }

                LaunchedEffect(info.directoryPath) {
                    filePathTextState.setTextAndPlaceCursorAtEnd(info.directoryPath)
                }

                Text(
                    TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.chooseDirectory.header"),
                )
                TextFieldWithBrowseButtonAndContextHelp(
                    filePathTextState,
                    descriptor,
                    contextHelpText = remember(fileName, projectName) {
                        TranslationsHelperBundle.message(
                            "action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.chooseDirectory.help",
                            listOf(
                                defaultTranslationsExportDirectory,
                                projectName,
                                "${fileName.removeSuffix(".xml")}.xlsx",
                            ).joinToString(File.separator)
                        )
                    },
                    Modifier.weight(1f),
                    readOnly = true,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                    contextHelpContentDescription = remember {
                        TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.chooseDirectory.help.contentDescription")
                    },
                ) { file ->
                    try {
                        if (file.isDirectory) {
                            info.directoryPath = file.path
                        } else {
                            info.directoryPath = ""
                        }
                    } catch (_: Exception) {
                        info.directoryPath = ""
                    }
                }
            }

            //...
        }
    }

    private class Info(
        directoryPath: String,
        val languages: MutableList<LanguageItemData> = mutableStateListOf(),
        onlyIfMissing: Boolean,
    ) {
        var directoryPath: String by mutableStateOf(directoryPath)
        var onlyIfMissing: Boolean by mutableStateOf(onlyIfMissing)
    }
}