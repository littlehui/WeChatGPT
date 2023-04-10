package com.idaymay.dzt.common.utils.number;

/**
 * 进制工具类
 *
 * @author cch on 2023/02/21
 */
public class HexadecimalUtil {

    private final static char[] char36 = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * 转 36 进制
     *
     * @param number 要转换的数字
     */
    public static String toHexadecimal36(int number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(char36[number % 36]);
            number = number / 36;
        }
        return builder.reverse().toString();
    }
}
