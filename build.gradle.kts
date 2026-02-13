import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.date
import java.util.*

plugins {
    java
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.intellij.platform")
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.changelog)
    alias(libs.plugins.breadmoirai.github.release)
}

group = providers.gradleProperty("groupId")

// Loading local.properties
val localProperties = Properties().apply {
    try {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            file.inputStream().use { load(it) }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
localProperties.forEach { (key, value) ->
    extra.set(key.toString(), value)
}
//---

dependencies {
    intellijPlatform {
        val idePath = project.findProperty("localIdePath")?.toString()
        if (idePath != null) {
            local(idePath)
        } else {
            androidStudio("2025.1.4.8")
        }

        bundledPlugin("org.jetbrains.kotlin")
        bundledPlugin("org.jetbrains.android")
        composeUI()
    }
    implementation(libs.poi.ooxml)
}

intellijPlatform {
    buildSearchableOptions = false

    pluginConfiguration {
        id = providers.gradleProperty("pluginId")
        version = providers.gradleProperty("pluginVersion")
        name = "Localizations Importer/Exporter"

        vendor {
            name = "Alfred Abdo"
            email = "alfredabdo13@gmail.com"
        }
        description = """
            This plugin allows to better handle localizations/translations:
            <ul>
            <li>Export strings.xml to Excel .xlsx</li>
            <li>Import from Excel .xlsx to strings.xml</li>
            </ul>
            <b>If you missed the ability to be able to select all strings from Android Studio's translations editor, welcome.</b>
        """.trimIndent()
        changeNotes = provider {
            changelog.render(
                Changelog.OutputType.HTML,
            )
        }

        ideaVersion {
            sinceBuild = "251"
            untilBuild = "253.*"
        }
    }

    signing {
        certificateChain = System.getenv("CERTIFICATE_CHAIN")
        privateKey = System.getenv("PRIVATE_KEY")
        val pass = System.getenv("PRIVATE_KEY_PASSWORD")
        if (!pass.isNullOrBlank()) {
            password = pass
        }
    }

    publishing {
        token = System.getenv("PUBLISH_TOKEN")
        channels = listOf("alpha")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    jvmToolchain(17)
}

changelog {
    version = providers.gradleProperty("pluginVersion")
    versionPrefix = ""
    path = file("CHANGELOG.md").canonicalPath
    header = provider { "[${version.get()}] (_${date()}_)" }
    keepUnreleasedSection = false
}

githubRelease {
    val version = providers.gradleProperty("pluginVersion").get()

    token(
        project.findProperty("githubToken")?.toString()
            ?: System.getenv("GITHUB_TOKEN")
    )
    owner = "AlfredAbdo"
    repo = "localizations-exporter"
    tagName = "v${version}"
    targetCommitish = "main"
    releaseName = "v${version}"
    generateReleaseNotes = true
}