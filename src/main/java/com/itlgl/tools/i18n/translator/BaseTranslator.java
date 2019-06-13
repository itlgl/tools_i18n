package com.itlgl.tools.i18n.translator;

import com.itlgl.tools.i18n.language.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public abstract class BaseTranslator implements Translator, LanguageAdapter {
    private String translateAppId;
    private String translateAppKey;
    private String translateAppUrl;

    public BaseTranslator(String translateAppId, String translateAppKey, String translateAppUrl) {
        this.translateAppId = translateAppId;
        this.translateAppKey = translateAppKey;
        this.translateAppUrl = translateAppUrl;
    }

    public String convertLanguage(Language language) {
        return getLanguageStr(language);
    }

    public static String httpGet(String url) throws Exception {
        try {
            URLConnection connection = new URL(url).openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String getTranslateAppId() {
        return translateAppId;
    }

    public String getTranslateAppKey() {
        return translateAppKey;
    }

    public String getTranslateAppUrl() {
        return translateAppUrl;
    }

    public void setTranslateAppId(String translateAppId) {
        this.translateAppId = translateAppId;
    }

    public void setTranslateAppKey(String translateAppKey) {
        this.translateAppKey = translateAppKey;
    }

    public void setTranslateAppUrl(String translateAppUrl) {
        this.translateAppUrl = translateAppUrl;
    }
}
