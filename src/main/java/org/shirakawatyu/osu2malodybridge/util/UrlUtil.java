package org.shirakawatyu.osu2malodybridge.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UrlUtil {
    /**
     * 获取url中的所有参数
     * @param url 要处理的url
     * @return 所有参数
     */
    public static Map<String, String> getParams(String url) {
        if (url == null) {
            return null;
        }
        HashMap<String, String> map = new HashMap<>();
        String[] split = url.split("&");
        for (int i = 0; i < split.length; i++) {
            String[] split1;
            if (i == 0) {
                String substring = split[i].substring(split[i].indexOf("?") + 1);
                split1 = substring.split("=");
            } else {
                split1 = split[i].split("=");
            }
            map.put(URLDecoder.decode(split1[0], StandardCharsets.UTF_8), URLDecoder.decode(split1[0], StandardCharsets.UTF_8));
        }
        return map;
    }

    /**
     * 去除url末尾的斜杠
     */
    public static String stripEndSlash(String url) {
        if (url.charAt(url.length() - 1) == '/') {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    /**
     * 给url末尾拼接上第一个参数
     */
    public static String getUrl(String url, String key, String value) {
        url = stripEndSlash(url);
        url += "?" + key + "=" + value;
        return url;
    }
}
