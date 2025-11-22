package alfredabdo.ide.plugins.translations.utils

import com.intellij.openapi.application.EDT
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.progress.blockingContext
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("UnstableApiUsage")
suspend fun <T> runWriteCommandAction(project: Project, action: () -> T): T {
    return withContext(Dispatchers.EDT) {
        blockingContext {
            WriteCommandAction.runWriteCommandAction(project, Computable(action))
        }
    }
}