package alfredabdo.ide.plugins.translations.importFromExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.importFromExcel.options.ImportSpecialCharactersHandling
import alfredabdo.ide.plugins.translations.ui.common.BoxWithScrollableContent
import alfredabdo.ide.plugins.translations.ui.common.ContextHelpButton
import alfredabdo.ide.plugins.translations.ui.common.IntTextField
import alfredabdo.ide.plugins.translations.ui.common.TextFieldWithBrowseButton
import alfredabdo.ide.plugins.translations.ui.group.AppGroupHeader
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.ListComboBox
import org.jetbrains.jewel.ui.component.Text

@Composable
internal fun ImportStringsFromExcelActionPanel(
    info: ImportStringsFromExcelActionInfo,
) {
    BoxWithScrollableContent(Modifier.fillMaxWidth()) { scrollState ->
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ImportFileOption(
                info.filePath,
                onFilePathChanged = { info.filePath = it },
                info.showFilePathError,
                onShowFilePathErrorChanged = { info.showFilePathError = it },
                Modifier.fillMaxWidth(),
            )

            ColumnIdOption(
                info.idColumnIndex,
                onIdColumnIndexChanged = { info.idColumnIndex = it },
                Modifier.fillMaxWidth(),
            )

            ImportLanguagesComponent(
                info.languages,
                onAddLanguage = {
                    info.languages += ImportLanguageItemData(info.languages.lastOrNull()?.columnIndex?.plus(1) ?: 1, "")
                },
                onDeleteLanguage = { language ->
                    info.languages.remove(language)
                },
                Modifier.fillMaxWidth(),
            )

            OverwriteStringsOption(
                info.shouldOverwriteResources,
                onEnabledChanged = { info.shouldOverwriteResources = it },
            )

            AdvancedOptionsGroup(
                info.advancedOptions,
                Modifier.fillMaxWidth(),
            )
        }
    }
}


@Composable
private fun ImportFileOption(
    filePath: String,
    onFilePathChanged: (String) -> Unit,
    showFilePathError: Boolean,
    onShowFilePathErrorChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val filePathTextState = rememberTextFieldState()
        val descriptor = remember {
            FileChooserDescriptorFactory.createSingleFileDescriptor("xlsx").apply {
                title =
                    TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.chooseFile.title")
                withFileFilter { file -> file.extension.equals("xlsx", ignoreCase = true) }
            }
        }

        LaunchedEffect(filePath) {
            if (filePath.isNotEmpty()) {
                onShowFilePathErrorChanged(false)
            }
            filePathTextState.setTextAndPlaceCursorAtEnd(filePath)
        }

        Text(
            TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.chooseFile.header"),
        )
        TextFieldWithBrowseButton(
            filePathTextState,
            descriptor,
            Modifier.weight(1f),
            readOnly = true,
            outline = if (showFilePathError) Outline.Error else Outline.None,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
        ) { file ->
            try {
                if (file.extension.equals("xlsx", ignoreCase = true)) {
                    onFilePathChanged(file.path)
                } else {
                    onFilePathChanged("")
                }
            } catch (_: Exception) {
                onFilePathChanged("")
            }
        }
    }
}

@Composable
private fun ColumnIdOption(
    idColumnIndex: Int,
    onIdColumnIndexChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val idColumnIndexTextState = rememberTextFieldState(idColumnIndex.toString())

        Text(
            TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.idColumnIndex.header"),
        )
        IntTextField(
            idColumnIndexTextState,
            Modifier.weight(1f),
            range = 0 until Int.MAX_VALUE,
            keyboardStep = 1,
            onValueChanged = { value -> onIdColumnIndexChanged(value ?: 0) },
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
        )
    }
}

@Composable
private fun OverwriteStringsOption(
    enabled: Boolean,
    onEnabledChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            enabled,
            onCheckedChange = onEnabledChanged,
        )
        Text(TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.overwriteStrings"))
        ContextHelpButton(
            TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.overwriteStrings.help"),
            contentDescription = TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.overwriteStrings.help.contentDescription"),
        )
    }
}

@Composable
private fun AdvancedOptionsGroup(
    options: ImportStringsFromExcelActionInfo.AdvancedOptions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AppGroupHeader(TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.general.advancedSettings"))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.advancedSettings.specialCharactersHandling.header"))
            ListComboBox(
                ImportSpecialCharactersHandling.entries.map { it.label },
                selectedIndex = options.specialCharactersHandling.id,
                onSelectedItemChange = { options.specialCharactersHandling = ImportSpecialCharactersHandling.entries[it] },
                Modifier.weight(1f),
            )
            ContextHelpButton(
                TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.advancedSettings.specialCharactersHandling.help"),
                contentDescription = TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.advancedSettings.specialCharactersHandling.help.contentDescription"),
            )
        }
    }
}