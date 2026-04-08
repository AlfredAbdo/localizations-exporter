package alfredabdo.ide.plugins.translations.importFromExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.ui.common.IntTextField
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
import org.jetbrains.jewel.ui.component.IconActionButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.typography

@Composable
fun ImportLanguagesComponent(
    states: List<ImportLanguageItemData>,
    onAddLanguage: () -> Unit,
    onDeleteLanguage: (state: ImportLanguageItemData) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.title"))
        Text(
            TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.import.subtitle"),
            style = JewelTheme.typography.small,
        )
        states.forEach { state ->
            key(state) {
                val columnIndexTextFieldState = rememberTextFieldState(state.columnIndex.toString())

                val codeTextFieldState = rememberTextFieldState(state.code)
                LaunchedEffect(codeTextFieldState.text) {
                    state.code = codeTextFieldState.text.toString()
                }


                ImportLanguageItem(
                    columnIndexTextFieldState,
                    onColumnIndexChanged = { value -> state.columnIndex = value ?: -1 },
                    codeTextFieldState,
                    onDelete = { onDeleteLanguage(state) },
                    Modifier.fillMaxWidth(),
                    isCurrentFile = state.isCurrentFile,
                )
            }
        }
        IconActionButton(
            AllIconsKeys.General.Add,
            TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.import.add.columnIndex"),
            onClick = onAddLanguage,
            Modifier
                .border(1.dp, JewelTheme.contentColor, RoundedCornerShape(8.dp))
                .width(32.dp),
        )
    }
}


@Stable
class ImportLanguageItemData(
    columnIndex: Int,
    code: String,
) {
    var columnIndex: Int by mutableIntStateOf(columnIndex)
    var code: String by mutableStateOf(code)
    var isCurrentFile: Boolean = false
        private set


    companion object {
        fun forCurrentFile(columnIndex: Int?, code: String?) = ImportLanguageItemData(
            columnIndex ?: 1,
            code ?: TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.code.default"),
        ).apply { isCurrentFile = true }
    }
}

@Composable
fun ImportLanguageItem(
    columnIndexTextFieldState: TextFieldState,
    onColumnIndexChanged: (value: Int?) -> Unit,
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
        IntTextField(
            columnIndexTextFieldState,
            Modifier.widthIn(min = 36.dp),
            range = 0 until Int.MAX_VALUE,
            keyboardStep = 1,
            placeholder = { Text(TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.columnIndex")) },
            onValueChanged = onColumnIndexChanged,
        )

        TextField(
            codeTextFieldState,
            Modifier.widthIn(min = 36.dp),
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