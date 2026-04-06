package alfredabdo.ide.plugins.translations.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.ui.icons.AllIconsKeys

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContextHelpButton(
    text: String,
    modifier: Modifier = Modifier,
    focusable: Boolean = true,
    contentDescription: String? = null,
) {
    Tooltip(
        tooltip = {
            Text(text)
        },
        modifier,
    ) {
        IconButton(
            onClick = {},
            Modifier,
            false,
            focusable,
        ) { state ->
            Icon(
                AllIconsKeys.General.ContextHelp,
                contentDescription,
            )
        }
    }
}