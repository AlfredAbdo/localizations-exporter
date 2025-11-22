package alfredabdo.ide.plugins.translations.utils

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.NlsContexts.DialogTitle
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.refreshAndFindVirtualFile
import com.intellij.ui.dsl.builder.COLUMNS_SHORT
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.columns
import org.jetbrains.kotlin.tools.projectWizard.core.asPath
import javax.swing.JTextField

fun Row.textFieldWithBrowseButtonFixed(
    @DialogTitle browseDialogTitle: String? = null,
    project: Project? = null,
    fileChooserDescriptor: FileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(),
    fileChosen: ((VirtualFile?) -> String)? = null
): Cell<TextFieldWithBrowseButton> =
    cell(
        TextFieldWithBrowseButton().apply {
            isOpaque = false
            textField.isOpaque = false

            addBrowseFolderListener(
                project,
                fileChooserDescriptor,
                object : TextComponentAccessor<JTextField> {
                    override fun getText(p0: JTextField?): String {
                        return p0?.text.orEmpty()
                    }

                    override fun setText(p0: JTextField?, p1: String) {
                        p0?.text = p1.asPath().refreshAndFindVirtualFile()
                            ?.let { fileChosen?.invoke(it) }
                            .orEmpty()
                    }
                }
            )

            FileChooserFactory.getInstance().installFileCompletion(
                textField,
                fileChooserDescriptor,
                true,
                null,
            )
        }
    )
        .columns(COLUMNS_SHORT)