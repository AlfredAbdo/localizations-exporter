@file:Suppress("UnstableApiUsage")

package alfredabdo.ide.plugins.translations.importFromExcel

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.asXMLFile
import alfredabdo.ide.plugins.translations.getLanguageCode
import alfredabdo.ide.plugins.translations.ui.common.ComposeDialogWrapper
import alfredabdo.ide.plugins.translations.ui.common.ContextHelpButton
import alfredabdo.ide.plugins.translations.ui.common.IntTextField
import alfredabdo.ide.plugins.translations.ui.common.TextFieldWithBrowseButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
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
import com.intellij.psi.xml.XmlFile
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.kotlin.tools.projectWizard.core.asPath

class ImportStringsFromExcelAction : AnAction() {

    override fun update(e: AnActionEvent) {
        val file = e.getData(LangDataKeys.PSI_FILE)?.asXMLFile()
        e.presentation.isVisible = file != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(p0: AnActionEvent) {
        val file = p0.getData(LangDataKeys.PSI_FILE)?.asXMLFile() ?: return

        val details = p0.project?.awaitInfo(file)
        details?.let {
            p0.project?.service<ImportStringsFromExcelService>()
                ?.import(file, it)
        }
    }


    private fun Project.awaitInfo(file: XmlFile): ImportStringsFromExcelService.Details? {
        val containingDirectoryLanguageCode = file.getLanguageCode()
        val info = Info(
            mutableStateListOf(
                ImportLanguageItemData.forCurrentFile(null, containingDirectoryLanguageCode),
            ),
        )

        val dialog = ComposeDialogWrapper(
            TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.title"),
            this,
            onValidate = {
                if (info.filePath.isEmpty()) {
                    info.showFilePathError = true
                    ValidationInfo(TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.chooseFile.missing"))
                } else if (info.languages.any { it.code.isBlank() }) {
                    ValidationInfo(TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.languages.emptyCode"))
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
                info.languages.map { language ->
                    ImportStringsFromExcelService.Details.Language(language.columnIndex, language.code, language.isCurrentFile)
                },
                info.shouldOverwriteResources,
            )
        else
            null
    }

    @Composable
    private fun Panel(
        info: Info,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
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

            ImportLanguagesComponent(
                info.languages,
                onAddLanguage = {
                    info.languages += ImportLanguageItemData(info.languages.lastOrNull()?.columnIndex?.plus(1) ?: 1, "")
                },
                onDeleteLanguage = { language ->
                    info.languages.remove(language)
                },
                Modifier.fillMaxWidth(),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    info.shouldOverwriteResources,
                    onCheckedChange = { info.shouldOverwriteResources = it },
                )
                Text(TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.overwriteStrings"))
                ContextHelpButton(
                    TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.overwriteStrings.help"),
                    contentDescription = TranslationsHelperBundle.message("action.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelAction.overwriteStrings.help.contentDescription"),
                )
            }
        }
    }

    private class Info(
        val languages: MutableList<ImportLanguageItemData>,
    ) {
        var filePath: String by mutableStateOf("")
        var idColumnIndex: Int by mutableIntStateOf(0)
        var shouldOverwriteResources: Boolean by mutableStateOf(false)

        var showFilePathError: Boolean by mutableStateOf(false)
    }
}