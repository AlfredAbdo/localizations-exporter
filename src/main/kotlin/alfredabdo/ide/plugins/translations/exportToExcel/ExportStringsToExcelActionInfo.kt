package alfredabdo.ide.plugins.translations.exportToExcel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class ExportStringsToExcelActionInfo(
    directoryPath: String,
    val languages: MutableList<ExportLanguageItemData>,
    onlyIfMissing: Boolean,
    advancedOptions: AdvancedOptions,
) {
    var directoryPath: String by mutableStateOf(directoryPath)
    var onlyIfMissing: Boolean by mutableStateOf(onlyIfMissing)
    var advancedOptions: AdvancedOptions by mutableStateOf(advancedOptions)

    internal class AdvancedOptions(
        ampersandConversion: Boolean,
        cdataUnwrapping: Boolean,
    ) {
        var ampersandConversion: Boolean by mutableStateOf(ampersandConversion)
        var cdataUnwrapping: Boolean by mutableStateOf(cdataUnwrapping)
    }
}