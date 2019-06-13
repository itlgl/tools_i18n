package com.itlgl.tools.i18n.translator;

import com.itlgl.tools.i18n.language.Language;

public interface Translator {
    String translate(String src, Language sourceLanguage, Language destLanguage) throws Exception;
}
