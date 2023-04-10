package com.idaymay.dzt.common.utils.string;

import java.util.regex.Pattern;

/**
 * @author littlehui
 * @date 2022/4/13 14:38
 * @return
 */
public class StringUtil {

    private static final Pattern chinesePattern = Pattern.compile("[\\u4e00-\\u9fa5]");

    /**
     * 是否为空
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return (s == null || "".equals(s.trim()));
    }

    /**
     * 是否不为空
     *
     * @param s
     * @return
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    /**
     * 是否包含中文
     *
     * @param s
     * @return
     */
    public static boolean isContainZH(String s) {
        return isEmpty(s) || chinesePattern.matcher(s).find();
    }

    public static String trim(String s) {
        return s != null ? s.trim() : null;
    }

    public static String filterRegexChar(String s) {
        return s;
    }
}
