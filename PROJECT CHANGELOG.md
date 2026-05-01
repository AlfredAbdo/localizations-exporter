# Project Changelog

## [0.1.0-alpha08] (_unreleased_)

- Updated Java SDK to 21 in order to better support Wayland in Linux.
- Added Jewel's VerticalScrollbar to both import and export to let the user see the content if many languages are added:
  - Had to set an exact size to the parent Box to 640 x 320 dp because the VerticalScrollbar always takes the max parent's height.

## [0.1.0-alpha07] (_28/04/2026_)

- Added advanced options for exporting (ampersand XML handling and cdata unwrapping).
- Added advanced options for importing (special characters handling).
- Recoded the panel composables to make the code more readable.
- Had to add _TranslationsHelperBundle.rawMessage()_ that relies on Java's ResourceBundle instead to allow rendering the & without issues.
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