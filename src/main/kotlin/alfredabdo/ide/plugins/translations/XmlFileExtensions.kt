package alfredabdo.ide.plugins.translations

import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile

internal fun PsiFile.asXMLFile(): XmlFile? = this as? XmlFile

internal fun XmlFile.getTranslatedFile(languageCode: String, fileName: String): XmlFile? =
    containingDirectory?.parentDirectory?.findSubdirectory("values-$languageCode")?.findFile(fileName) as? XmlFile?