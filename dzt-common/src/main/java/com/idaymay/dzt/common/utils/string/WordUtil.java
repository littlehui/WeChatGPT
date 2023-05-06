package com.idaymay.dzt.common.utils.string;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2021/8/13 18:13
 **/
public class WordUtil {

    /**
     * 统计一段英文单词的数量
     *
     * @param text
     * @return java.lang.Integer
     * @author littlehui
     * @date 2021/11/15 15:29
     */
    public static Integer wordCount(String text) {
        if (text == null) {
            return 0;
        } else {
            return text.split("\\s+").length;
        }
    }
}
