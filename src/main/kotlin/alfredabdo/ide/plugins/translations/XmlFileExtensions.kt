package alfredabdo.ide.plugins.translations

import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile

internal fun PsiFile.asXMLFile(): XmlFile? = this as? XmlFile

internal fun XmlFile.getTranslatedFile(languageCode: String?, fileName: String): XmlFile? =
    containingDirectory?.parentDirectory
        ?.findSubdirectory(if (languageCode != null) "values-$languageCode" else "values")
        ?.findFile(fileName) as? XmlFile?

internal fun XmlFile.getLanguageCode(): String? =
    containingDirectory?.name?.substringAfterLast("-", "")?.takeIf { it.isNotEmpty() }