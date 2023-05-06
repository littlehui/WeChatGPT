package com.idaymay.dzt.common.utils.string;

/**
 * @author littlehui
 * @version 1.0
 * @description TODO
 * @date 2022/4/13 14:35
 */
public class IPUtil {

    public static long ip2Long(String ip) {
        if (StringUtil.isEmpty(ip)) {
            return 0;
        }
        String[] ips = ip.split("[.]");
        long num = 16777216L * Long.parseLong(ips[0]) + 65536L * Long.parseLong(ips[1]) + 256 * Long.parseLong(ips[2]) + Long.parseLong(ips[3]);
        return num;
    }

    /**
     * @description: TODO
     * @author littlehui
     * @date 2022/4/13 14:40
     * @version 1.0
     */
    public static String long2Ip(long ipLong) {
        //long ipLong = 1037591503;
        long mask[] = {0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000};
        long num = 0;
        StringBuffer ipInfo = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            num = (ipLong & mask[i]) >> (i * 8);
            if (i > 0) ipInfo.insert(0, ".");
            ipInfo.insert(0, Long.toString(num, 10));
        }
        return ipInfo.toString();
    }

}
