# Project Changelog

## [0.1.0-alpha09] (_25/07/2026_)

- Updated IntelliJ Platform Plugin version to _2.16.0_ where the KotlinCompile bug is resolved.
- Removed buildSearchableOptions = false, as it works now without setting it to false.
- Updated dependencies to latest available versions.
- Updated to support until Android Studio Idea build version _261.*_ (Quail).
    - But had to increase the minimum supported Android Studio version to _253.*_ (Panda) due to the updated dependencies.
- Added pluginVerification config to intellijPlatform for using verifyPlugin to detect incompatibilities with the Android Studio versions
  covered.
- Removed @Suppress ("UnstableApiUsage") from ImportStringsFromExcelAction.kt as it does seem like it is needed anymore.
- Switched from using _runReadAction_ to using suspendable _readAction_ because it is deprecated in build _261.*_ (Quail) in
  ExportStringsToExcelService.kt.

## [0.1.0-alpha08] (_02/05/2026_)

- Updated Java SDK to 21 in order to better support Wayland in Linux.
- Added Jewel's VerticalScrollbar to both import and export to let the user see the content if many languages are added:
    - Had to set an exact size to the parent Box to 640 x 320 dp because the VerticalScrollbar always takes the max parent's height.
- Improved the export and import UIs by moving some components and fixing some messages to be clearer.
- Updated README.md and the screenshots to reflect all the new changes.

## [0.1.0-alpha07] (_28/04/2026_)

- Added advanced options for exporting (ampersand XML handling and cdata unwrapping).
- Added advanced options for importing (special characters handling).
- Recoded the panel composables to make the code more readable.
- Had to add _TranslationsHelperBundle.rawMessage ()_ that relies on Java's ResourceBundle instead to allow rendering the & without issues.
- Fixed undo action not being available when importing strings into an existing file:
    - The way the virtual files were created had to be massively refactored in order to properly add the changes to the Undo history.
- Updated Kotlin version to _2.3.21_.
- ! Could not update IntelliJ Platform Plugin version to _2.15.0_ because of the KotlinCompile bug (again).

## [0.1.0-alpha06] (_20/04/2026_)

- Added titles for notification groups.
- Reorganized the code, and separated the Compose UIs into separate files.
- Updated the IntelliJ Platform Plugin version to _2.14.0_ to fix the bug "NoClassDefFoundError:
  org.jetbrains.kotlin.gradle.tasks.KotlinCompile", and the previous workaround with the Kotlin dependency in settings.gradle.kts was
  reversed.

### Planned

- Adding Compose Previews to the import and export panels:
    - Adding the ability to preview Compose in this project does not seem straight-forward, and is delayed until the IntelliJ Platform
      Plugin makes it easier.