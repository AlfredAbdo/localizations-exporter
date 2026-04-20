package alfredabdo.ide.plugins.translations.importFromExcel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class ImportStringsFromExcelActionInfo(
    val languages: MutableList<ImportLanguageItemData>,
) {
    var filePath: String by mutableStateOf("")
    var idColumnIndex: Int by mutableIntStateOf(0)
    var shouldOverwriteResources: Boolean by mutableStateOf(false)

    var showFilePathError: Boolean by mutableStateOf(false)
}