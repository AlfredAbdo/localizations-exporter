package alfredabdo.ide.plugins.translations.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.android.tools.adtui.compose.LocalProject
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.refreshAndFindVirtualFile
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.IconActionButton
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.component.painterResource
import org.jetbrains.kotlin.tools.projectWizard.core.asPath

@Composable
fun TextFieldWithBrowseButton(
    state: TextFieldState,
    fileChooserDescriptor: FileChooserDescriptor,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    outline: Outline = Outline.None,
    textStyle: TextStyle = LocalTextStyle.current,
    provideDefaultSelection: () -> VirtualFile? = {
        try {
            state.text.toString().asPath().refreshAndFindVirtualFile()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    },
    onFileSelected: (file: VirtualFile) -> Unit,
) {
    TextField(
        state,
        modifier,
        trailingIcon = {
            BrowseButton(
                fileChooserDescriptor,
                provideDefaultSelection = provideDefaultSelection,
            ) { file ->
                onFileSelected(file)
            }
        },
        readOnly = readOnly,
        outline = outline,
        textStyle = textStyle,
    )
}

@Composable
fun TextFieldWithBrowseButtonAndContextHelp(
    state: TextFieldState,
    fileChooserDescriptor: FileChooserDescriptor,
    contextHelpText: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    outline: Outline = Outline.None,
    textStyle: TextStyle = LocalTextStyle.current,
    provideDefaultSelection: () -> VirtualFile? = {
        try {
            state.text.toString().asPath().refreshAndFindVirtualFile()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    },
    contextHelpContentDescription: String? = null,
    onFileSelected: (file: VirtualFile) -> Unit,
) {
    TextField(
        state,
        modifier,
        trailingIcon = {
            Row {
                BrowseButton(
                    fileChooserDescriptor,
                    provideDefaultSelection = provideDefaultSelection,
                ) { file ->
                    onFileSelected(file)
                }
                Spacer(Modifier.width(2.dp))
                ContextHelpButton(
                    contextHelpText,
                    contentDescription = contextHelpContentDescription,
                )
            }
        },
        readOnly = readOnly,
        outline = outline,
        textStyle = textStyle,
    )
}

@Composable
fun BrowseButton(
    fileChooserDescriptor: FileChooserDescriptor,
    modifier: Modifier = Modifier,
    provideDefaultSelection: () -> VirtualFile? = { null },
    onFileSelected: (file: VirtualFile) -> Unit,
) {
    val project = LocalProject.current

    IconActionButton(
        painterResource("general/openDisk.svg"),
        "Browse",
        onClick = {
            FileChooser.chooseFile(fileChooserDescriptor, project, provideDefaultSelection(), onFileSelected)
        },
        modifier,
    )
}