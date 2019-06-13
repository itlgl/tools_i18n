package com.itlgl.tools.i18n.document.impl;

import com.itlgl.tools.i18n.document.DocumentHandler;
import com.itlgl.tools.i18n.document.TranslatorDocument;
import com.itlgl.tools.i18n.language.Language;
import com.itlgl.tools.i18n.translator.Translator;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.*;

public class AndroidDocumentHandler extends DocumentHandler {

    public AndroidDocumentHandler(String sourceDir, Language sourceLanguage, List<Language> destLanguageList, Translator translator) {
        super(sourceDir, sourceLanguage, destLanguageList, translator);
    }

    @Override
    public List<TranslatorDocument> loadDocuments() throws Exception {
        List<TranslatorDocument> translatorDocumentList = new ArrayList<>();

        File file = new File(getSourceDir());
        File[] files = file.listFiles();
        if (files != null) {
            for (Language destLanguage : getDestLanguageList()) {
                for (File doc : files) {
                    try {
                        if (doc.isFile() && doc.getName().endsWith(".xml")) {
                            System.out.println("find " + doc.getName());
                            Map<String, String> resMap = convertStringXmlToMap(doc);

                            TranslatorDocument translatorDocument = new TranslatorDocument(doc, getSourceLanguage(), destLanguage);

                            for (String key : resMap.keySet()) {
                                String value = resMap.get(key);
                                translatorDocument.getSourceMap().put(key, value);
                            }

                            {
                                // 查找是否存在目标文件
                                // 如果存在的话，直接将dest的值也设置上，这样外面可以判断是否还需要继续翻译此字段
                                File xmlFileGrandParent = new File(getSourceDir()).getParentFile();
                                File destDir = new File(xmlFileGrandParent, getDestFileDirNameByLanguage(translatorDocument.getDestLanguage()));
                                File destFile = new File(destDir, translatorDocument.getSourceFile().getName());
                                if (destFile.exists() && destFile.isFile()) {
                                    try {
                                        Map<String, String> resMap2 = convertStringXmlToMap(destFile);

                                        for (String key : resMap2.keySet()) {
                                            String value = resMap2.get(key);
                                            translatorDocument.getDestMap().put(key, value);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            if (translatorDocument.getSourceMap().size() != 0) {
                                translatorDocumentList.add(translatorDocument);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return translatorDocumentList;
    }

    @Override
    public void writeDocuments(List<TranslatorDocument> documentList) throws Exception {
        for (TranslatorDocument translatorDocument : documentList) {
            // 比对一下，目标文件的item是否存在
            // 将目标文件的item保留下来，当做预留翻译项
            File xmlFileGrandParent = new File(getSourceDir()).getParentFile();
            File destDir = new File(xmlFileGrandParent, getDestFileDirNameByLanguage(translatorDocument.getDestLanguage()));
            File destFile = new File(destDir, translatorDocument.getSourceFile().getName());
            if (destFile.exists() && destFile.isFile()) {
                try {
                    Map<String, String> resMap = convertStringXmlToMap(destFile);
                    for (String key : resMap.keySet()) {
                        String value = resMap.get(key);
                        if (translatorDocument.getDestMap().containsKey(key)) {
                            translatorDocument.getDestMap().put(key, value);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Document document = DocumentHelper.createDocument();
            Element resources = document.addElement("resources");
            for (String key : translatorDocument.getDestMap().keySet()) {
                String value = translatorDocument.getDestMap().get(key);
                if(key.startsWith("string-array-")) {
                    if(value != null) {
                        String keyArray = key.substring("string-array-1".length());
                        List<Element> stringArrayElements = resources.elements("string-array");
                        Element elementArray = null;
                        for (Element element : stringArrayElements) {
                            if(keyArray.equals(element.attribute("name").getValue())) {
                                elementArray = element;
                            }
                        }
                        if(elementArray == null) {
                            elementArray = resources.addElement("string-array")
                                    .addAttribute("name", keyArray);
                        }
                        elementArray.addElement("item").addText(value);
                    }
                } else {
                    if (value != null) {
                        resources.addElement("string")
                                .addAttribute("name", key)
                                .addText(value);
                    }
                }
            }

            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8"); //设置XML文档的编码类型
            format.setIndent(true); //设置是否缩进
            format.setIndent("\t"); //以tab方式实现缩进
            format.setNewlines(true); //设置是否换行

            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(destFile), format);
            xmlWriter.write(document);
            xmlWriter.close();
        }
    }

    public static String getDestFileDirNameByLanguage(Language language) {
        switch (language) {
            case zh_CN:
                return "values-zh-rCN";
            case zh_TW:
                return "values-zh-rTW";
            default:
                return "values-" + language.name();
        }
    }

    public static Map<String, String> convertStringXmlToMap(File stringXmlFile) throws Exception {
        Map<String, String> resultMap = new LinkedHashMap<>();

        if (stringXmlFile.exists() && stringXmlFile.isFile()) {
            try {
                Document document = DocumentHelper.parseText(readFile(stringXmlFile));
                Element rootElement = document.getRootElement();
                Iterator iterator = rootElement.elementIterator();
                while (iterator.hasNext()) {
                    Element element = (Element) iterator.next();
                    if ("string".equalsIgnoreCase(element.getName())) {
                        String key = element.attribute("name").getValue();
                        String value = element.getText();
                        if (value == null) {
                            value = "";
                        }

                        resultMap.put(key, value);
                    } else if("string-array".equalsIgnoreCase(element.getName())) {
                        String key = element.attribute("name").getValue();
                        Iterator iterator2 = element.elementIterator();
                        int index = 0;
                        while (iterator2.hasNext()) {
                            Element item = (Element) iterator2.next();
                            String text = item.getText();
                            if(text == null) {
                                text = "";
                            }

                            resultMap.put("string-array-" + index + key, text);
                            index++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultMap;
    }
}
