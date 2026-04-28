package alfredabdo.ide.plugins.translations.importFromExcel.options

import TranslationsHelperBundle


sealed interface ImportSpecialCharactersHandling {

    companion object {
        val entries = arrayOf(
            None,
            XmlCharacter,
            CDATA,
        )
    }


    val id: Int
    val label: String


    data object None : ImportSpecialCharactersHandling {
        override val id: Int = 0
        override val label: String
            get() = TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.data.importSpecialCharactersHandling.none.title")
    }

    data object XmlCharacter : ImportSpecialCharactersHandling {
        override val id: Int = 1
        override val label: String
            get() = TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.data.importSpecialCharactersHandling.xmlCharacter.title")
    }

    data object CDATA : ImportSpecialCharactersHandling {
        override val id: Int = 2
        override val label: String
            get() = TranslationsHelperBundle.message("alfredabdo.ide.plugins.translations.data.importSpecialCharactersHandling.cdata.title")
    }
}