package alfredabdo.ide.plugins.translations.exportToExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.settings.ui.defaultTranslationsExportDirectory
import alfredabdo.ide.plugins.translations.ui.common.BoxWithScrollableContent
import alfredabdo.ide.plugins.translations.ui.common.ContextHelpButton
import alfredabdo.ide.plugins.translations.ui.common.TextFieldWithBrowseButtonAndContextHelp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.GroupHeader
import org.jetbrains.jewel.ui.component.Text
import java.io.File

@Composable
internal fun ExportStringsToExcelActionPanel(
    fileName: String,
    projectName: String,
    info: ExportStringsToExcelActionInfo,
) {
    BoxWithScrollableContent(Modifier.fillMaxWidth()) { scrollState ->
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                TranslationsHelperBundle.message(
                    "action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.confirmation.message",
                    fileName,
                ),
            )

            ExportLanguagesComponent(
                info.languages,
                onlyIfMissing = info.onlyIfMissing,
                onOnlyIfMissingChanged = { info.onlyIfMissing = it },
                onAddLanguage = {
                    info.languages += ExportLanguageItemData("", "")
                },
                onDeleteLanguage = { language ->
                    info.languages.remove(language)
                },
                Modifier.fillMaxWidth(),
            )

            ExportPathOption(
                info.directoryPath,
                onDirectoryPathChanged = { info.directoryPath = it },
                fileName,
                projectName,
                Modifier.fillMaxWidth(),
            )

            AdvancedOptionsGroup(
                info.advancedOptions,
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
            )
        }
    }
}


@Composable
private fun ExportPathOption(
    directoryPath: String,
    onDirectoryPathChanged: (String) -> Unit,
    fileName: String,
    projectName: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier,
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

        LaunchedEffect(directoryPath) {
            filePathTextState.setTextAndPlaceCursorAtEnd(directoryPath)
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
                    onDirectoryPathChanged(file.path)
                } else {
                    onDirectoryPathChanged("")
                }
            } catch (_: Exception) {
                onDirectoryPathChanged("")
            }
        }
    }
}

@Composable
private fun AdvancedOptionsGroup(
    options: ExportStringsToExcelActionInfo.AdvancedOptions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GroupHeader(TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.general.advancedSettings"))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                options.ampersandConversion,
                { options.ampersandConversion = it },
            )
            Text(TranslationsHelperBundle.rawMessage("action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.advancedSettings.ampersandConversion"))
            ContextHelpButton(
                TranslationsHelperBundle.rawMessage("action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.advancedSettings.ampersandConversion.help"),
                contentDescription = TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.advancedSettings.ampersandConversion.help.contentDescription"),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                options.cdataUnwrapping,
                { options.cdataUnwrapping = it },
            )
            Text(TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.advancedSettings.cdataUnwrapping"))
            ContextHelpButton(
                TranslationsHelperBundle.rawMessage("action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.advancedSettings.cdataUnwrapping.help"),
                contentDescription = TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.exportToExcel.ExportStringsToExcelAction.advancedSettings.cdataUnwrapping.help.contentDescription"),
            )
        }
    }
}