package alfredabdo.ide.plugins.translations.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@Service(Service.Level.APP)
@State(name = "TranslationsAppSettingsService", storages = [Storage("translations_app_settings_service.xml")])
internal class TranslationsAppSettingsService :
    SerializablePersistentStateComponent<TranslationsSettingsServiceState>(TranslationsSettingsServiceState()) {

    var exportDirectoryPath: String?
        get() = state.exportDirectoryPath
        set(value) {
            updateState {
                it.copy(exportDirectoryPath = value)
            }
        }
}

internal fun appSettings(): TranslationsAppSettingsService = service()


@Service(Service.Level.PROJECT)
@State(name = "TranslationsProjectSettingsService", storages = [Storage("translations_project_settings_service.xml")])
internal class TranslationsProjectSettingsService(private val project: Project) :
    SerializablePersistentStateComponent<TranslationsSettingsServiceState>(TranslationsSettingsServiceState()) {

    var overrideGlobal: Boolean
        get() = state.overrideGlobal
        set(value) {
            updateState {
                it.copy(overrideGlobal = value)
            }
        }

    var exportDirectoryPath: String?
        get() = state.exportDirectoryPath
        set(value) {
            updateState {
                it.copy(exportDirectoryPath = value)
            }
        }
}

internal fun Project.projectSettings(): TranslationsProjectSettingsService = this.service()


internal data class TranslationsSettingsServiceState(
    @JvmField val overrideGlobal: Boolean = false,
    @JvmField val exportDirectoryPath: String? = null,
)