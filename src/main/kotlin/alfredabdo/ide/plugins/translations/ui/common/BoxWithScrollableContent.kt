package alfredabdo.ide.plugins.translations.ui.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.VerticalScrollbar

@Composable
inline fun BoxWithScrollableContent(
    modifier: Modifier = Modifier,
    content: BoxScope.(scrollState: ScrollState) -> Unit,
) {
    Box(modifier) {
        val scrollState = rememberScrollState()

        content(scrollState)

        VerticalScrollbar(
            scrollState,
            Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .offset(x = 8.dp),
        )
    }
}