package alfredabdo.ide.plugins.translations.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.Text

@Composable
fun ConfigurableWithGlobalOverrideUI(
    isGlobalOverride: Boolean,
    onUpdateGlobalOverride: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = isGlobalOverride,
                onCheckedChange = onUpdateGlobalOverride,
            )
            Spacer(Modifier.width(8.dp))
            Text("Override global settings")
        }
        Spacer(Modifier.height(8.dp))
        content()
    }
}