package alfredabdo.ide.plugins.translations.settings

import com.intellij.openapi.project.Project

internal fun Project.resolveExportDirectoryPath(): String? = resolvedSavedState.exportDirectoryPath


internal val Project.resolvedSavedState: TranslationsSettingsServiceState
    get() {
        val projectService = projectSettings()
        return if (projectService.state.overrideGlobal) {
            projectService.state
        } else {
            appSettings().state
        }
    }