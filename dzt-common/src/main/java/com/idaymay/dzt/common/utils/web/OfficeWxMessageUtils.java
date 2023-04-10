package com.idaymay.dzt.common.utils.web;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import io.micrometer.core.instrument.util.StringEscapeUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author littlehui
 * @version 1.0
 * @description TODO
 * @date 2022/12/20 14:36
 */
@Slf4j
public class OfficeWxMessageUtils {

    private static String wechatNotifyUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send";

    /**
     * 企业微信群机器人推送
     * @param title
     * @param content
     */
    public static void officeWeixinNotify(String title,String content, String key){
        HttpRequest request = HttpUtil.createPost(wechatNotifyUrl + "?key=" + key);
        String markdown = "# "+title+"\n"+
                "> "+ StringEscapeUtils.escapeJson(content).replace("},{","},\n> {")+"";
        String body = "{\n" +
                "        \"msgtype\": \"markdown\",\n" +
                "        \"markdown\": {\n" +
                "            \"content\": \""+markdown+"\",\n" +
                "            \"mentioned_list\":[\"@all\"]\n" +
                "        }\n" +
                "   }";
        request.body(body, ContentType.JSON.getValue());
        HttpResponse response = request.execute();
    }
}
