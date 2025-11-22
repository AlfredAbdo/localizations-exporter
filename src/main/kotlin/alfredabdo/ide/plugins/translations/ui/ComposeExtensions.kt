package alfredabdo.ide.plugins.translations.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.awt.ComposePanel
import com.android.tools.adtui.compose.LocalProject
import com.intellij.openapi.project.Project
import org.jetbrains.jewel.bridge.JewelComposePanel
import javax.swing.JComponent

@Suppress("FunctionName")
fun JewelComposePanel(
    project: Project?,
    config: ComposePanel.() -> Unit = {},
    content: @Composable () -> Unit,
): JComponent = JewelComposePanel(config) {
    CompositionLocalProvider(
        LocalProject provides project,
    ) {
        content()
    }
}