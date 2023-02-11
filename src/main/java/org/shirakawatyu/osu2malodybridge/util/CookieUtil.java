package org.shirakawatyu.osu2malodybridge.util;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;

import java.util.List;

public class CookieUtil {
    /**
     * 查找CookieStore中某个键的值，不存在时返回null
     */
    public static String findCookieValue(BasicCookieStore cookieStore, String name) {
        Cookie cookie = findCookie(cookieStore, name);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }
    /**
     * 根据键查找CookieStore中的某个cookie，不存在时返回null
     */
    public static Cookie findCookie(BasicCookieStore cookieStore, String name) {
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }
}
