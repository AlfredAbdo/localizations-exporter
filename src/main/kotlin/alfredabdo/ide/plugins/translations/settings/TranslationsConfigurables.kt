@file:OptIn(ExperimentalComposeUiApi::class)

package alfredabdo.ide.plugins.translations.settings

import TranslationsHelperBundle
import alfredabdo.ide.plugins.translations.settings.ui.ConfigurableWithGlobalOverrideUI
import alfredabdo.ide.plugins.translations.settings.ui.TranslationsConfigurableUI
import alfredabdo.ide.plugins.translations.settings.ui.TranslationsConfigurableUIState
import alfredabdo.ide.plugins.translations.settings.ui.TranslationsConfigurableUIStateWithGlobalOverride
import alfredabdo.ide.plugins.translations.ui.JewelComposePanel
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.unit.dp
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import org.jetbrains.annotations.NonNls
import javax.swing.JComponent

class TranslationsProjectConfigurable(private val project: Project) : SearchableConfigurable {

    private val isProjectLevel = !project.isDefault

    private val service = project.projectSettings()
    private val appService get() = appSettings()
    private val stateHolder = TranslationsConfigurableUIStateWithGlobalOverride(TranslationsConfigurableUIState())
    private var component: ComposePanel? = null


    override fun getId(): @NonNls String = "project.alfredabdo.ide.plugins.translations.settings.TranslationsHelper"
    override fun getDisplayName(): @NlsContexts.ConfigurableName String =
        TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.plugin.name")

    override fun createComponent(): JComponent {
        initUIState()

        return JewelComposePanel(if (isProjectLevel) project else null, { component = this }) {
            if (isProjectLevel) {
                ConfigurableWithGlobalOverrideUI(
                    stateHolder.overrideGlobal,
                    onUpdateGlobalOverride = { stateHolder.overrideGlobal = it },
                    Modifier
                        .padding(16.dp)
                        .widthIn(max = 640.dp),
                ) {
                    TranslationsConfigurableUI(
                        stateHolder.state,
                        Modifier
                            .fillMaxWidth(),
                    )
                }
            } else {
                TranslationsConfigurableUI(
                    stateHolder.state,
                    Modifier
                        .padding(16.dp)
                        .widthIn(max = 640.dp),
                )
            }
        }
    }

    override fun isModified(): Boolean = stateHolder.isModified

    override fun apply() {
        if (isProjectLevel) {
            service.overrideGlobal = stateHolder.overrideGlobal
            if (stateHolder.overrideGlobal) {
                service.exportDirectoryPath = stateHolder.state.exportDirectoryPath
            } else {
                //ignore the project override
            }
        } else {
            appService.exportDirectoryPath = stateHolder.state.exportDirectoryPath
        }

        stateHolder.persist()
    }

    override fun reset() {
        initUIState()
    }

    override fun disposeUIResources() {
        component?.dispose()
        component = null
    }


    private fun initUIState() {
        if (isProjectLevel) {
            val isOverrideGlobal = service.overrideGlobal
            stateHolder.updateFrom(
                isOverrideGlobal,
                if (isOverrideGlobal) service.state else appService.state,
            )
        } else {
            stateHolder.updateFrom(
                false,
                appService.state,
            )
        }
    }
}


