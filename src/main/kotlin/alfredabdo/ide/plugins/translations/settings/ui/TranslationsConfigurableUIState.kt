package alfredabdo.ide.plugins.translations.settings.ui

import alfredabdo.ide.plugins.translations.settings.TranslationsSettingsServiceState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TranslationsConfigurableUIState {

    var exportDirectoryPath by mutableStateOf("")

    var initialExportDirectoryPath: String = ""

    val isModified: Boolean
        get() = exportDirectoryPath != initialExportDirectoryPath


    var showExportDirectoryPathError: Boolean by mutableStateOf(false)


    internal fun updateFrom(state: TranslationsSettingsServiceState) {
        initialExportDirectoryPath = state.exportDirectoryPath.orEmpty()
        exportDirectoryPath = initialExportDirectoryPath
    }

    internal fun persist() {
        initialExportDirectoryPath = exportDirectoryPath
    }
}

class TranslationsConfigurableUIStateWithGlobalOverride(
    val state: TranslationsConfigurableUIState,
) {
    var overrideGlobal by mutableStateOf(false)

    var initialOverrideGlobal: Boolean = false

    val isModified: Boolean
        get() = overrideGlobal != initialOverrideGlobal || (overrideGlobal && state.isModified)


    internal fun updateFrom(isOverrideGlobal: Boolean, state: TranslationsSettingsServiceState) {
        initialOverrideGlobal = isOverrideGlobal
        overrideGlobal = initialOverrideGlobal

        this.state.updateFrom(state)
    }

    internal fun persist() {
        initialOverrideGlobal = overrideGlobal

        this.state.persist()
    }
}