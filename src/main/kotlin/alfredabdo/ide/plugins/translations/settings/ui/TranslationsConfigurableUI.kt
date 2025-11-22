package alfredabdo.ide.plugins.translations.settings.ui

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.ui.TextFieldWithBrowseButtonAndContextHelp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
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
import org.jetbrains.jewel.ui.component.Text
import java.io.File

@Composable
fun TranslationsConfigurableUI(
    state: TranslationsConfigurableUIState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val filePathTextState = rememberTextFieldState(state.exportDirectoryPath)
        val descriptor = remember {
            FileChooserDescriptorFactory.createSingleFileDescriptor("xlsx").apply {
                title =
                    TranslationsHelperBundle.message("preferences.alfredabdo.ide.plugins.translations.TranslationsAppConfigurable.chooseDirectory.title")
                withFileFilter { file -> file.extension.equals("xlsx", ignoreCase = true) }
            }
        }

        LaunchedEffect(state.exportDirectoryPath) {
            if (state.exportDirectoryPath.isNotEmpty()) {
                state.showExportDirectoryPathError = false
            }
            filePathTextState.setTextAndPlaceCursorAtEnd(state.exportDirectoryPath)
        }

        Text(
            TranslationsHelperBundle.message("preferences.alfredabdo.ide.plugins.translations.TranslationsAppConfigurable.chooseDirectory.header"),
        )
        TextFieldWithBrowseButtonAndContextHelp(
            filePathTextState,
            descriptor,
            contextHelpText = remember {
                TranslationsHelperBundle.message(
                    "preferences.alfredabdo.ide.plugins.translations.TranslationsAppConfigurable.chooseDirectory.help",
                    defaultTranslationsExportDirectory + File.separator,
                    File.separator,
                    ".xlsx",
                )
            },
            Modifier.weight(1f),
            readOnly = true,
            outline = if (state.showExportDirectoryPathError) Outline.Error else Outline.None,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            contextHelpContentDescription = remember {
                TranslationsHelperBundle.message("preferences.alfredabdo.ide.plugins.translations.TranslationsAppConfigurable.chooseDirectory.help.contentDescription")
            },
        ) { file ->
            try {
                if (file.extension.equals("xlsx", ignoreCase = true)) {
                    state.exportDirectoryPath = file.path
                } else {
                    state.exportDirectoryPath = ""
                }
            } catch (_: Exception) {
                state.exportDirectoryPath = ""
            }
        }
    }
}


internal val defaultTranslationsExportDirectory
    get() = listOf(
        System.getProperty("user.home"),
        "Desktop",
        "Localizations-Helper",
    ).joinToString(File.separator)