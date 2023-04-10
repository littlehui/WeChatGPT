package com.idaymay.dzt.app.web.utils;

import com.idaymay.dzt.common.utils.obj.ObjectUtil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * web工具类.
 *
 * @author littlehui
 * @date 2013-2-8 下午5:53:44
 */
public class WebUtil {

    /**
     * 判断是否是ajax请求.
     *
     * @param request
     * @return
     * @author littlehui
     * @date 2012-7-30 下午02:59:10
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        return !ObjectUtil.isEmpty(request.getHeader("X-Requested-With"));
    }

    /**
     * 获取远程访问的IP地址.
     *
     * @param request
     * @return
     * @author littlehui
     * @date 2012-9-18 上午09:02:09
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                ip = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) { // "***.***.***.***".length() = 15
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    /**
     * 获得编码后的url地址
     *
     * @param url url地址
     * @return
     * @author qingwu
     * @date 2013-9-3 上午09:00:00
     */
    public static String encodedUri(String url) {
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url)
                .build();
        String encodedUri = uriComponents.encode().toUriString();
        return encodedUri;
    }

}
