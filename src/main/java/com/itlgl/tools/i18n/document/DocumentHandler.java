package com.itlgl.tools.i18n.document;

import com.itlgl.tools.i18n.language.Language;
import com.itlgl.tools.i18n.translator.Translator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DocumentHandler {
    public abstract List<TranslatorDocument> loadDocuments() throws Exception;
    public abstract void writeDocuments(List<TranslatorDocument> documentList) throws Exception;


    private List<TranslatorDocument> translatorDocumentList = new ArrayList<>();
    private String sourceDir;
    private Language sourceLanguage;
    private List<Language> destLanguageList;
    private Translator translator;

    public DocumentHandler(String sourceDir, Language sourceLanguage, List<Language> destLanguageList, Translator translator) {
        this.sourceDir = sourceDir;
        this.sourceLanguage = sourceLanguage;
        this.destLanguageList = destLanguageList;
        this.translator = translator;
    }

    public List<TranslatorDocument> getTranslatorDocumentList() {
        return translatorDocumentList;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public Translator getTranslator() {
        return translator;
    }

    public Language getSourceLanguage() {
        return sourceLanguage;
    }

    public List<Language> getDestLanguageList() {
        return destLanguageList;
    }

    public void translate() {
        try {
            List<TranslatorDocument> translatorDocuments = loadDocuments();
            translatorDocumentList.addAll(translatorDocuments);

            System.out.println("list string files");
            for (TranslatorDocument translatorDocument : translatorDocumentList) {
                System.out.println("file name=" + translatorDocument.getSourceFile().getName() +
                        ",[" + translatorDocument.getSourceLanguage() + " -> " + translatorDocument.getDestLanguage() + "]" +
                        ",source size=" + translatorDocument.getSourceMap().size() +
                        ",dest size=" + translatorDocument.getDestMap().size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (TranslatorDocument translatorDocument : translatorDocumentList) {
            Map<String, String> sourceMap = translatorDocument.getSourceMap();
            Map<String, String> destMap = translatorDocument.getDestMap();
            for (String key : sourceMap.keySet()) {
                String value = sourceMap.get(key);

                String translateResult = null;
                // 如果目标文件翻译已存在，不访问网络节省时间
                if (destMap.containsKey(key)) {
                    translateResult = destMap.get(key);
                } else {
                    try {
                        translateResult = translator.translate(value, translatorDocument.getSourceLanguage(), translatorDocument.getDestLanguage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (translateResult == null) {
                    translateResult = "";
                }

                translatorDocument.getDestMap().put(key, translateResult);
            }

        }

        try {
            writeDocuments(translatorDocumentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFile(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        fis.close();

        return result.toString();
    }
}
