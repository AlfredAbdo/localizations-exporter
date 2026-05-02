package alfredabdo.ide.plugins.translations.ui.group

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.annotations.Nls
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.GroupHeader

@Composable
fun AppGroupHeader(
    @Nls text: String,
    modifier: Modifier = Modifier,
) {
    GroupHeader(
        text,
        modifier,
        textStyle = JewelTheme.defaultTextStyle.copy(fontWeight = FontWeight.Bold),
    )
}