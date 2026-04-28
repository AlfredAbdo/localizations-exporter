# Changelog

## [0.1.0-alpha07] (_28/04/2026_)

### Added

- Advanced options for exporting localizations:
  - Convert special ampersand character in XML (`&amp;`) to its raw representation (`&`).
  - Extract the raw string from inside CDATA wrappers.
- Advanced options for importing localizations:
  - Special character handling (for now, only `&` is handled):
    - _None_: No changes will be done to strings, even the ones that should be handled.
    - _Convert to XML special character_: `&` will be converted to its XML representation (`&amp;`).
    - _Wrap in CDATA_: any strings with `&` will be wrapped with the CDATA tag.

### Modified

- Fixed undo action not being available when importing strings into an existing file.

## [0.1.0-alpha06] (_20/04/2026_)

### Added

- Ability to right-click an XML file in the Project View tool window instead of having to open the file

## [0.1.0-alpha05] (_08/04/2026_)

### Added

- Ability to import multiple languages at once from an Excel file
- New option: "overwrite strings"; enabling this will replace exiting string resources, i.e. conflicting strings with the same "name" attribute results in updating the value to match the last one resolved

### Modified

- Fixed bug where the Excel workbook was not being closed in ImportStringsFromExcelService.kt after importing
- Added ability to remove from the list of languages (export and import); now, it will be clearer which language will read from the currently opened file

## [0.1.0-alpha04] (_07/04/2026_)

### Added

- Ability to add multiple languages to export (including the file already opened, which will always be first), and customize the headers
- New option: "only if missing"; enabling this will only export the resources that are missing from the other included languages

### Modified

- Removed the check for empty resources; now, you can export a file even without resources that will only include the headers

## [0.1.0-alpha03] (_06/04/2026_)

### Modified

- Updated dependencies

## [0.1.0-alpha02] (_13/02/2026_)

### Modified

- Changes to the GitHub action flow and tested it for this release (no changes for the plugin itself)

### Added

- Initial release

## [0.1.0-alpha01] (_11/22/2025_)

### Added

- Initial release
