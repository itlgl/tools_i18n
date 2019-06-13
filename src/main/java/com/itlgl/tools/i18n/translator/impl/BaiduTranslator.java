package com.itlgl.tools.i18n.translator.impl;

import com.google.gson.Gson;
import com.itlgl.tools.i18n.translator.BaseTranslator;
import com.itlgl.tools.i18n.language.Language;
import com.itlgl.tools.i18n.utils.MD5Utils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 百度翻译实现<br/>
 *
 * <b>语言列表</b>
 * 源语言语种不确定时可设置为 auto，目标语言语种不可设置为 auto。<br/>
 * 语言简写 	名称
 * auto 	自动检测
 * zh 	中文
 * en 	英语
 * yue 	粤语
 * wyw 	文言文
 * jp 	日语
 * kor 	韩语
 * fra 	法语
 * spa 	西班牙语
 * th 	泰语
 * ara 	阿拉伯语
 * ru 	俄语
 * pt 	葡萄牙语
 * de 	德语
 * it 	意大利语
 * el 	希腊语
 * nl 	荷兰语
 * pl 	波兰语
 * bul 	保加利亚语
 * est 	爱沙尼亚语
 * dan 	丹麦语
 * fin 	芬兰语
 * cs 	捷克语
 * rom 	罗马尼亚语
 * slo 	斯洛文尼亚语
 * swe 	瑞典语
 * hu 	匈牙利语
 * cht 	繁体中文
 * vie 	越南语
 */
public class BaiduTranslator extends BaseTranslator {
    Map<Language, String> languageMap = new HashMap<>();
    {
        languageMap.put(Language.zh_CN, "zh");
        languageMap.put(Language.zh_TW, "cht");
        languageMap.put(Language.fr, "fra");
        languageMap.put(Language.ja, "jp");
        languageMap.put(Language.ko_KR, "kor");
    }

    public BaiduTranslator(String translateAppId, String translateAppKey, String translateAppUrl) {
        super(translateAppId, translateAppKey, translateAppUrl);
    }

    @Override
    public String translate(String src, Language sourceLanguage, Language destLanguage) throws Exception {
        Random random = new Random();
        int randomInt = random.nextInt();

        String sign = sign(src, randomInt);
        String url = String.format("%s?from=%s&to=%s&appid=%s&salt=%d&q=%s&sign=%s",
                getTranslateAppUrl(), convertLanguage(sourceLanguage), convertLanguage(destLanguage), getTranslateAppId(),
                randomInt, URLEncoder.encode(src, "utf-8"), sign);
        System.out.println("REQUEST " + src + " ===> " + url);

        String result = httpGet(url);
        BaiduTranslateResult jsonResult = new Gson().fromJson(result, BaiduTranslateResult.class);
        if (jsonResult != null &&
                jsonResult.getTrans_result() != null &&
                jsonResult.getTrans_result().get(0) != null) {
            String translateResult = jsonResult.getTrans_result().get(0).getDst();
            System.out.println("TRANSLATE " + src + " ===> " + translateResult);
            return translateResult;
        } else {
            throw new Exception("json 解析错误");
        }
    }

    private String sign(String content, int randomInt) {
        return MD5Utils.md5(getTranslateAppId() + content + randomInt + getTranslateAppKey());
    }

    @Override
    public String getLanguageStr(Language language) {
        if(languageMap.containsKey(language)) {
            return languageMap.get(language);
        }
        return language.name();
    }
}
