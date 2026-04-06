package alfredabdo.ide.plugins.translations.ui.theme

import androidx.compose.runtime.Composable
import org.jetbrains.jewel.bridge.theme.SwingBridgeTheme
import org.jetbrains.jewel.foundation.ExperimentalJewelApi

@Suppress("UnstableApiUsage")
@OptIn(ExperimentalJewelApi::class)
@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    SwingBridgeTheme(content)
}