package alfredabdo.ide.plugins.translations

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.LangDataKeys

class TranslationsActionGroup : DefaultActionGroup() {

    override fun update(e: AnActionEvent) {
        val file = e.getData(LangDataKeys.PSI_FILE)?.asXMLFile()
        e.presentation.isVisible = file != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}