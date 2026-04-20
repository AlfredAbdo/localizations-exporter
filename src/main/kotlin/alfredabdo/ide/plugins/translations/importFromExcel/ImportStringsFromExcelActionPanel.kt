package alfredabdo.ide.plugins.translations.importFromExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.ui.common.ContextHelpButton
import alfredabdo.ide.plugins.translations.ui.common.IntTextField
import alfredabdo.ide.plugins.translations.ui.common.TextFieldWithBrowseButton
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
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.Text

@Composable
internal fun ImportStringsFromExcelActionPanel(
    info: ImportStringsFromExcelActionInfo,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
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

            LaunchedEffect(info.filePath) {
                if (info.filePath.isNotEmpty()) {
                    info.showFilePathError = false
                }
                filePathTextState.setTextAndPlaceCursorAtEnd(info.filePath)
            }

            Text(
                TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.chooseFile.header"),
            )
            TextFieldWithBrowseButton(
                filePathTextState,
                descriptor,
                Modifier.weight(1f),
                readOnly = true,
                outline = if (info.showFilePathError) Outline.Error else Outline.None,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            ) { file ->
                try {
                    if (file.extension.equals("xlsx", ignoreCase = true)) {
                        info.filePath = file.path
                    } else {
                        info.filePath = ""
                    }
                } catch (_: Exception) {
                    info.filePath = ""
                }
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val idColumnIndexTextState = rememberTextFieldState(info.idColumnIndex.toString())

            Text(
                TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.idColumnIndex.header"),
            )
            IntTextField(
                idColumnIndexTextState,
                Modifier.weight(1f),
                range = 0 until Int.MAX_VALUE,
                keyboardStep = 1,
                onValueChanged = { value ->
                    info.idColumnIndex = value ?: 0
                },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            )
        }

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

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                info.shouldOverwriteResources,
                onCheckedChange = { info.shouldOverwriteResources = it },
            )
            Text(TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.overwriteStrings"))
            ContextHelpButton(
                TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.overwriteStrings.help"),
                contentDescription = TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.overwriteStrings.help.contentDescription"),
            )
        }
    }
}