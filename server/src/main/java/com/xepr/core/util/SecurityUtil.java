package com.xepr.core.util;

import jakarta.servlet.http.HttpServletRequest;

import java.security.SecureRandom;

public final class SecurityUtil {

    private static final SecureRandom sr = new SecureRandom();

    private SecurityUtil() {
    }

    public static int generateAuthCode() {
        return sr.nextInt(100000, 1000000);
    }

    public static String getClientIpAddress(HttpServletRequest hsr) {
        if (hsr == null) {
            throw new NullPointerException("Http servlet request cannot be null");
        }

        String ip = hsr.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            ip = ip.split(",")[0].strip();
            return ip;
        }

        return hsr.getRemoteAddr();
    }
}
