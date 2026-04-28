package alfredabdo.ide.plugins.translations.importFromExcel

import alfredabdo.ide.plugins.translations.importFromExcel.options.ImportSpecialCharactersHandling
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class ImportStringsFromExcelActionInfo(
    val languages: MutableList<ImportLanguageItemData>,
    advancedOptions: AdvancedOptions,
) {
    var filePath: String by mutableStateOf("")
    var idColumnIndex: Int by mutableIntStateOf(0)
    var shouldOverwriteResources: Boolean by mutableStateOf(false)
    var advancedOptions: AdvancedOptions by mutableStateOf(advancedOptions)

    var showFilePathError: Boolean by mutableStateOf(false)

    internal class AdvancedOptions(
        specialCharactersHandling: ImportSpecialCharactersHandling,
    ) {
        var specialCharactersHandling: ImportSpecialCharactersHandling by mutableStateOf(specialCharactersHandling)
    }
}