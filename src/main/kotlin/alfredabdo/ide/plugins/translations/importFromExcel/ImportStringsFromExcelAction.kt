@file:Suppress("UnstableApiUsage")

package alfredabdo.ide.plugins.translations.importFromExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.asXMLFile
import alfredabdo.ide.plugins.translations.ui.ComposeDialogWrapper
import alfredabdo.ide.plugins.translations.ui.ContextHelpButton
import alfredabdo.ide.plugins.translations.ui.IntTextField
import alfredabdo.ide.plugins.translations.ui.TextFieldWithBrowseButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.tools.idea.util.toIoFile
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.refreshAndFindVirtualFile
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.kotlin.tools.projectWizard.core.asPath

class ImportStringsFromExcelAction : AnAction() {

    override fun update(e: AnActionEvent) {
        val file = e.getData(LangDataKeys.PSI_FILE)?.asXMLFile()
        e.presentation.isVisible = file != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(p0: AnActionEvent) {
        val file = p0.getData(LangDataKeys.PSI_FILE)?.asXMLFile() ?: return

        val details = p0.project?.awaitInfo()
        details?.let {
            p0.project?.service<ImportStringsFromExcelService>()
                ?.import(file, it)
        }
    }


    private fun Project.awaitInfo(): ImportStringsFromExcelService.Details? {
        val info = Info()

        val dialog = ComposeDialogWrapper(
            TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.title"),
            this,
            onValidate = {
                if (info.filePath.isEmpty()) {
                    info.showFilePathError = true
                    ValidationInfo(TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.chooseFile.missing"))
                } else {
                    null
                }
            },
        ) {
            Panel(info)
        }

        return if (dialog.showAndGet())
            ImportStringsFromExcelService.Details(
                info.filePath.asPath().refreshAndFindVirtualFile()!!.toIoFile(),
                info.idColumnIndex,
                info.translatedLanguageCode,
                info.translatedColumnIndex,
            )
        else
            null
    }

    @Composable
    private fun Panel(
        info: Info,
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val filePathTextState = rememberTextFieldState()
                val descriptor = remember {
                    FileChooserDescriptorFactory.createSingleFileDescriptor("xlsx").apply {
                        title =
                            TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.chooseFile.title")
                        withFileFilter { file -> file.extension.equals("xlsx", ignoreCase = true) }
                    }
                }

                LaunchedEffect(info.filePath) {
                    if (info.filePath.isNotEmpty()) {
                        info.showFilePathError = false
                    }
                    filePathTextState.setTextAndPlaceCursorAtEnd(info.filePath)
                }

                Text(
                    TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.chooseFile.header"),
                )
                TextFieldWithBrowseButton(
                    filePathTextState,
                    descriptor,
                    Modifier.weight(1f),
                    readOnly = true,
                    outline = if (info.showFilePathError) Outline.Error else Outline.None,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                ) { file ->
                    try {
                        if (file.extension.equals("xlsx", ignoreCase = true)) {
                            info.filePath = file.path
                        } else {
                            info.filePath = ""
                        }
                    } catch (_: Exception) {
                        info.filePath = ""
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val idColumnIndexTextState = rememberTextFieldState(info.idColumnIndex.toString())

                Text(
                    TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.idColumnIndex.header"),
                )
                IntTextField(
                    idColumnIndexTextState,
                    Modifier.weight(1f),
                    range = 0 until Int.MAX_VALUE,
                    keyboardStep = 1,
                    onValueChanged = { value ->
                        info.idColumnIndex = value ?: 0
                    },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val translatedLanguageCodeTextState = rememberTextFieldState(info.translatedLanguageCode)

                LaunchedEffect(translatedLanguageCodeTextState.text) {
                    info.translatedLanguageCode = translatedLanguageCodeTextState.text.toString()
                }

                Text(
                    TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.translatedLanguageCode.header"),
                )
                TextField(
                    translatedLanguageCodeTextState,
                    Modifier.weight(1f),
                    trailingIcon = {
                        ContextHelpButton(
                            TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.translatedLanguageCode.help"),
                            contentDescription = TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.translatedLanguageCode.help.contentDescription"),
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val translatedColumnIndexTextState = rememberTextFieldState(info.translatedColumnIndex.toString())

                Text(
                    TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.translatedColumnIndex.header"),
                )
                IntTextField(
                    translatedColumnIndexTextState,
                    Modifier.weight(1f),
                    range = 0 until Int.MAX_VALUE,
                    keyboardStep = 1,
                    onValueChanged = { value ->
                        info.translatedColumnIndex = value ?: 0
                    },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                )
            }
        }
    }

    private class Info {
        var filePath: String by mutableStateOf("")
        var idColumnIndex: Int by mutableIntStateOf(0)
        var translatedLanguageCode: String by mutableStateOf("")
        var translatedColumnIndex: Int by mutableIntStateOf(2)

        var showFilePathError: Boolean by mutableStateOf(false)
    }
}