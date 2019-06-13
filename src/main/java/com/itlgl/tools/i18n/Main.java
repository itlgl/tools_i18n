package com.itlgl.tools.i18n;

import com.itlgl.tools.i18n.translator.Translator;
import com.itlgl.tools.i18n.translator.impl.BaiduTranslator;
import com.itlgl.tools.i18n.document.DocumentHandler;
import com.itlgl.tools.i18n.document.impl.AndroidDocumentHandler;
import com.itlgl.tools.i18n.language.Language;

import java.util.*;

public class Main {
    // --- config parameter ---
    Map<String, String> sourceMap = new HashMap<>();

    Language sourceLanguage = Language.zh_CN;
    List<Language> destLanguageList = new ArrayList<>();
    {
        destLanguageList.add(Language.en);
        destLanguageList.add(Language.zh_TW);
    }
    String sourcePlatform = "android";

    String sourceFileDir = "D:\\projects\\ligl\\tools_i18n\\android_demo_values\\values";

    String translatePlatform = "baidu";
    String translateAppId = "";
    String translateAppKey = "";
    String translateAppUrl = "https://api.fanyi.baidu.com/api/trans/vip/i18n";
    // ------

    private Translator translator;
    private DocumentHandler documentHandler;

    public static void main(String[] args) {
        Main main = new Main();
        main.initConfig();
        main.translate();
    }

    private void initConfig() {
        // todo read config

        System.out.println("sourceLanguage = " + sourceLanguage);
        System.out.println("destLanguage = " + destLanguageList);
        System.out.println();
        System.out.println("sourcePlatform = " + sourcePlatform);
        System.out.println();
        System.out.println("translatePlatform = " + translatePlatform);
        System.out.println("translateAppId = " + translateAppId);
        System.out.println("translateAppKey = " + translateAppKey);
        System.out.println("translateAppUrl = " + translateAppUrl);

        initTranslator();
        initDocumentHandler();
    }

    private void initTranslator() {
        if("baidu".equalsIgnoreCase(translatePlatform)) {
            translator = new BaiduTranslator(translateAppId, translateAppKey, translateAppUrl);
        }

        if(translator == null) {
            throw new RuntimeException("不支持的翻译平台：" + translatePlatform);
        }
    }

    private void initDocumentHandler() {
        if("android".equalsIgnoreCase(sourcePlatform)) {
            documentHandler = new AndroidDocumentHandler(sourceFileDir, sourceLanguage, destLanguageList, translator);
        }

        if(documentHandler == null) {
            throw new RuntimeException("不支持的资源文件格式：" + sourcePlatform);
        }
    }

    private void translate() {
        documentHandler.translate();

//        Document document = DocumentHelper.createDocument();
//        Element resources = document.addElement("resources");
//        Element stringArrayElement = resources.addElement("string-array")
//                .addAttribute("name", "test");
//        List<Element> stringArrayElements = resources.elements("string-array");
//        Element elementArray = null;
//        for (Element element : stringArrayElements) {
//            System.out.println(element.attribute("name").getValue());
//            if("test".equals(element.attribute("name").getValue())) {
//                elementArray = element;
//            }
//        }
//        System.out.println(elementArray);
    }
}
