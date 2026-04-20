# Project Changelog

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