package alfredabdo.ide.plugins.translations.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tools.adtui.compose.LocalProject
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.NlsContexts
import org.jetbrains.jewel.bridge.JewelComposePanel
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.JComponent

class ComposeDialogWrapper(
    @NlsContexts.DialogTitle title: String,
    private val project: Project,
    private val onValidate: () -> ValidationInfo? = { null },
    private val actionsProvider: (ComposeDialogWrapper.() -> Array<Action>)? = null,
    private val content: @Composable () -> Unit,
) : DialogWrapper(project) {

    init {
        this.title = title
        init()
    }

    override fun createCenterPanel(): JComponent {
        return JewelComposePanel {
            CompositionLocalProvider(
                LocalProject provides project, //fixme just to make sure
            ) {
                Box(
                    Modifier
                        .padding(16.dp)
                        .widthIn(max = 640.dp), //fixme don't know the logic used by DialogPanel
                ) {
                    content()
                }
            }
        }
    }

    override fun doValidate(): ValidationInfo? = onValidate()

    override fun createActions(): Array<Action> {
        return actionsProvider?.invoke(this) ?: super.createActions()
    }


    fun generateDialogWrapperAction(
        name: String,
        config: AbstractAction.() -> Unit = {},
        onAction: (e: ActionEvent?) -> Unit,
    ): Action = object : DialogWrapperAction(name) {
        override fun doAction(e: ActionEvent?) {
            onAction(e)
        }
    }.apply(config)
}