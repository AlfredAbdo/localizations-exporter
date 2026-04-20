package alfredabdo.ide.plugins.translations.exportToExcel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class ExportStringsToExcelActionInfo(
    directoryPath: String,
    val languages: MutableList<ExportLanguageItemData>,
    onlyIfMissing: Boolean,
) {
    var directoryPath: String by mutableStateOf(directoryPath)
    var onlyIfMissing: Boolean by mutableStateOf(onlyIfMissing)
}