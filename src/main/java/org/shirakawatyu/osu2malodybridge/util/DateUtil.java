package org.shirakawatyu.osu2malodybridge.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    /**
     * 返回时间对应的时间戳
     * @param pattern 格式
     * @param date 字符串日期
     * @return 时间戳
     */
    public static long parseDate(String pattern, String date) {
        if (pattern == null || date == null) {
            return 0;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            return simpleDateFormat.parse(date).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
