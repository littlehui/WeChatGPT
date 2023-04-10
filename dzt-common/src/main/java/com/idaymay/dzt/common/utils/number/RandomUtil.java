package com.idaymay.dzt.common.utils.number;

import java.util.Random;

/**
 * @ClassName RandomUtil
 * @author littlehui
 * @date 2021/7/18 10:42
 * @version 1.0
 **/
public class RandomUtil {

    private static final Random RANDOM = new Random();

    private static final char[] NUMBERS = "0123456789".toCharArray();

    private static final char[] LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private static final char[] NUMBERS_AND_LETTERS = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    /**
     * 产生length位的随机字符串
     *
     * @description: TODO
     * @author littlehui
     * @date 2022/4/13 14:25
     * @version 1.0
     */
    public static String randomStr(int length) {
        if (length < 1) {
            return null;
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = NUMBERS_AND_LETTERS[RANDOM.nextInt(72)];
        }
        return new String(randBuffer);
    }

    /**
     * 产生length长度的字母字符
     *
     * @description: TODO
     * @author littlehui
     * @date 2022/4/13 14:25
     * @version 1.0
     */
    public static String randomLetter(int length) {
        if (length < 1) {
            return null;
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = LETTERS[RANDOM.nextInt(51)];
        }
        return new String(randBuffer);
    }

    /**
     * 产生length位的随机数字
     *
     * @description: TODO
     * @author littlehui
     * @date 2022/4/13 14:26
     * @version 1.0
     */
    public static String randomNum(int length) {
        if (length < 1) {
            return null;
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = NUMBERS[RANDOM.nextInt(10)];
            if (i == 0) {
                if (randBuffer[i] == '0') {
                    i--;
                }
            }
        }
        return new String(randBuffer);
    }

    /**
     * 随即产生min到max之间的随机数,包含max和min
     *
     * @description: TODO
     * @author littlehui
     * @date 2022/4/13 14:26
     * @version 1.0
     */
    public static int randomInt(int min, int max) {
        if (min == max) {
            return min;
        }
        return RANDOM.nextInt(max + 1) % (max - min + 1) + min;
    }
}
