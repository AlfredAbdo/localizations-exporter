package alfredabdo.ide.plugins.translations.ui

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
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.IconActionButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.icons.AllIconsKeys

@Composable
fun LanguagesComponent(
    states: List<LanguageItemData>,
    onlyIfMissing: Boolean,
    onOnlyIfMissingChanged: (newState: Boolean) -> Unit,
    onAddLanguage: () -> Unit,
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
            val labelTextFieldState = rememberTextFieldState(state.label)
            LaunchedEffect(labelTextFieldState.text) {
                state.label = labelTextFieldState.text.toString()
            }

            val codeTextFieldState = rememberTextFieldState(state.code)
            LaunchedEffect(codeTextFieldState.text) {
                state.code = codeTextFieldState.text.toString()
            }


            LanguageItem(
                labelTextFieldState,
                codeTextFieldState,
                Modifier.fillMaxWidth(),
                codeEnabled = !state.isDefault,
            )
        }
        IconActionButton(
            AllIconsKeys.General.Add,
            TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.add.contentDescription"),
            onClick = onAddLanguage,
            Modifier
                .border(1.dp, JewelTheme.contentColor, RoundedCornerShape(8.dp))
                .width(32.dp),
        )
    }
}


class LanguageItemData(
    label: String,
    code: String,
) {
    var label: String by mutableStateOf(label)
    var code: String by mutableStateOf(code)
    var isDefault: Boolean = false
        private set


    companion object {
        fun default(label: String?, code: String?) = LanguageItemData(
            label ?: TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.label.default"),
            code ?: TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.ui.languagesComponent.code.default"),
        ).apply { isDefault = true }
    }
}

@Composable
fun LanguageItem(
    labelTextFieldState: TextFieldState,
    codeTextFieldState: TextFieldState,
    modifier: Modifier = Modifier,
    codeEnabled: Boolean = true,
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
            enabled = codeEnabled,
        )
    }
}