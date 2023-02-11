package org.shirakawatyu.osu2malodybridge.util;

public class StringUtil {
    /**
     * 将字符串中包含的windows不允许作为文件名的字符去掉
     * @param str 要处理的字符串
     * @return 处理好的字符串
     */
    public static String stripInvalid(String str) {
        return str.replace(",", "")
                .replace(":", "")
                .replace("/", "")
                .replace("\\", "")
                .replace("?", "");
    }
}
