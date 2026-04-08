package alfredabdo.ide.plugins.translations.exportToExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.ui.common.ContextHelpButton
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.IconActionButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.icons.AllIconsKeys

@Composable
fun ExportLanguagesComponent(
    states: List<ExportLanguageItemData>,
    onlyIfMissing: Boolean,
    onOnlyIfMissingChanged: (newState: Boolean) -> Unit,
    onAddLanguage: () -> Unit,
    onDeleteLanguage: (state: ExportLanguageItemData) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.title"))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                onlyIfMissing,
                onOnlyIfMissingChanged,
            )
            Text(TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.onlyIfMissing"))
            ContextHelpButton(
                TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.onlyIfMissing.help"),
                contentDescription = TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.onlyIfMissing.help.contentDescription"),
            )
        }
        states.forEach { state ->
            key(state) {
                val labelTextFieldState = rememberTextFieldState(state.label)
                LaunchedEffect(labelTextFieldState.text) {
                    state.label = labelTextFieldState.text.toString()
                }

                val codeTextFieldState = rememberTextFieldState(state.code)
                LaunchedEffect(codeTextFieldState.text) {
                    state.code = codeTextFieldState.text.toString()
                }


                ExportLanguageItem(
                    labelTextFieldState,
                    codeTextFieldState,
                    onDelete = { onDeleteLanguage(state) },
                    Modifier.fillMaxWidth(),
                    isCurrentFile = state.isCurrentFile,
                )
            }
        }
        IconActionButton(
            AllIconsKeys.General.Add,
            TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.export.add.contentDescription"),
            onClick = onAddLanguage,
            Modifier
                .border(1.dp, JewelTheme.contentColor, RoundedCornerShape(8.dp))
                .width(32.dp),
        )
    }
}


@Stable
class ExportLanguageItemData(
    label: String,
    code: String,
) {
    var label: String by mutableStateOf(label)
    var code: String by mutableStateOf(code)
    var isCurrentFile: Boolean = false
        private set


    companion object {
        fun forCurrentFile(label: String?, code: String?) = ExportLanguageItemData(
            label ?: TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.label.default"),
            code ?: TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.code.default"),
        ).apply { isCurrentFile = true }
    }
}

@Composable
fun ExportLanguageItem(
    labelTextFieldState: TextFieldState,
    codeTextFieldState: TextFieldState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    isCurrentFile: Boolean = false,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextField(
            labelTextFieldState,
            Modifier.widthIn(min = 84.dp),
            placeholder = { Text(TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.label.placeholder")) },
        )

        TextField(
            codeTextFieldState,
            Modifier.widthIn(min = 84.dp),
            placeholder = { Text(TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.code.placeholder")) },
            enabled = !isCurrentFile,
        )

        if (isCurrentFile) {
            Text(
                TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.currentFile"),
                fontStyle = FontStyle.Italic,
            )
        }

        IconActionButton(
            AllIconsKeys.General.Delete,
            TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.delete"),
            onClick = onDelete,
            Modifier
                .border(1.dp, JewelTheme.contentColor, RoundedCornerShape(8.dp))
                .width(24.dp),
        )
    }
}