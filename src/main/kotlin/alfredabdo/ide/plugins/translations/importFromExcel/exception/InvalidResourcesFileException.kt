package alfredabdo.ide.plugins.translations.importFromExcel.exception

class InvalidResourcesFileException : Exception(
    TranslationsHelperBundle.message("service.alfredabdo.ide.plugins.translations.importFromExcel.ImportStringsFromExcelService.error.invalidDestination"),
)