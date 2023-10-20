package com.gumdom.boot.infrastructure;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public interface IBasicServiceController extends IBasicExtension, IBridgeAcrossProxyService {

    /**
     * 缓存类
     */
    default ICaching getCached() {
        return MapCached.getInstance();
    }

    default String selectLocale() {
        return "";
    }

    /**
     * 拿点Nginx的ip吧
     */
    default String getRequestIP(HttpServletRequest servletRequest) {
        if (servletRequest == null) {
            return "";
        }
        String ip = servletRequest.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip) || "null".equalsIgnoreCase(ip)) {
            ip = servletRequest.getHeader("Proxy-Client-IP");
        }

        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip) || "null".equalsIgnoreCase(ip)) {
            ip = servletRequest.getHeader("WL-Proxy-Client-IP");
        }

        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip) || "null".equalsIgnoreCase(ip)) {
            ip = servletRequest.getHeader("HTTP_CLIENT_IP");
        }

        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip) || "null".equalsIgnoreCase(ip)) {
            ip = servletRequest.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip) || "null".equalsIgnoreCase(ip)) {
            ip = servletRequest.getHeader("X-Real-IP");
        }

        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip) || "null".equalsIgnoreCase(ip)) {
            ip = servletRequest.getRemoteAddr();
        }

        if (this.isNullOrEmpty(ip)) {
            return ip;
        }

        int index = ip.indexOf(',');
        if (index > 0) {
            return ip.substring(0,index);
        }
        return ip;
    }
}
