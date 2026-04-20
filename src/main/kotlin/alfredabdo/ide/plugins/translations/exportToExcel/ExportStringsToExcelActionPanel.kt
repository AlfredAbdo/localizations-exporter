package alfredabdo.ide.plugins.translations.exportToExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.settings.ui.defaultTranslationsExportDirectory
import alfredabdo.ide.plugins.translations.ui.common.TextFieldWithBrowseButtonAndContextHelp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
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
import org.jetbrains.jewel.ui.component.Text
import java.io.File

@Composable
internal fun ExportStringsToExcelActionPanel(
    fileName: String,
    projectName: String,
    info: ExportStringsToExcelActionInfo,
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
    }
}