package com.idaymay.dzt.common.utils.string;

/**
 * 密码加解密算法
 * @author littlehui
 * @date 2021/7/3 16:08
 * @version 1.0
 **/
public class PasswordUtil {

    /**
     * key长度必须为8
     */
    private static final String DEFAULT_KEY = "TINYCODE";

    /**
     * 密码默认Key加密
     * @param password
     * @author littlehui
     * @date 2021/11/15 15:36
     * @return java.lang.String
     */
    public static String encode(String password) {
        try {
            return DesUtil.encrypt(password, DEFAULT_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    /**
     * 默认Key解密
     * @param encodedPassword
     * @author littlehui
     * @date 2021/11/15 15:38
     * @return java.lang.String
     */
    public static String decode(String encodedPassword) {
        try {
            return DesUtil.decrypt(encodedPassword, DEFAULT_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedPassword;
    }
    /**
     * 通过key进行加密
     * @param password
     * @param key
     * @author littlehui
     * @date 2021/11/15 15:36
     * @return java.lang.String
     */
    public static String encode(String password, String key) {
        try {
            return DesUtil.encrypt(password, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    /**
     * 通过Key进行解密
     * @param encodedPassword
     * @param key
     * @author littlehui
     * @date 2021/11/15 15:38
     * @return java.lang.String
     */
    public static String decode(String encodedPassword, String key) {
        try {
            return DesUtil.decrypt(encodedPassword, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedPassword;
    }

    public static void main(String[] args) throws Exception {
        String data = "ghcwx1203323423";
        System.err.println(encode(data));
        String encrypedData = "b82ab72ffa286144ea31c123a2aeb38f80eec2b7215388e9";
        //System.out.println(decode(encrypedData));
        //System.out.println(Base64.decode("a0g13c9cj05whhxt"));
    }
}
