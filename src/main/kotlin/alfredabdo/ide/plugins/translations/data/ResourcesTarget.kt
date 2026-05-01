package alfredabdo.ide.plugins.translations.data

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.psi.PsiDirectory
import com.intellij.psi.xml.XmlFile

sealed interface ResourcesTarget {
    class Directory(val directory: PsiDirectory) : ResourcesTarget
    class File(val file: XmlFile) : ResourcesTarget
}


internal fun AnActionEvent.extractResourcesTarget(): ResourcesTarget? =
    (getData(LangDataKeys.PSI_FILE) as? XmlFile)
        ?.let { file -> ResourcesTarget.File(file) }
        ?: (getData(LangDataKeys.PSI_ELEMENT) as? PsiDirectory)
            ?.takeIf { directory -> directory.isResDirectory() || directory.isValuesDirectory() }
            ?.let { directory -> ResourcesTarget.Directory(directory) }

internal fun PsiDirectory.isResDirectory(): Boolean = name == "res"

internal fun PsiDirectory.isValuesDirectory(): Boolean = name.startsWith("values") && parentDirectory?.isResDirectory() == true