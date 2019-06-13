package com.itlgl.tools.i18n.document;

import com.itlgl.tools.i18n.language.Language;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 每个资源文件对应一个TranslatorDocument，这里同时会存放翻译的结果
 */
public class TranslatorDocument {
    private File sourceFile;
    private Language sourceLanguage;
    private Language destLanguage;
    private Map<String, String> sourceMap = new LinkedHashMap<>();
    private Map<String, String> destMap = new LinkedHashMap<>();

    public TranslatorDocument(File sourceFile, Language sourceLanguage, Language destLanguage) {
        this.sourceFile = sourceFile;
        this.sourceLanguage = sourceLanguage;
        this.destLanguage = destLanguage;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public Language getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(Language sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public Language getDestLanguage() {
        return destLanguage;
    }

    public void setDestLanguage(Language destLanguage) {
        this.destLanguage = destLanguage;
    }

    public Map<String, String> getSourceMap() {
        return sourceMap;
    }

    public Map<String, String> getDestMap() {
        return destMap;
    }
}
