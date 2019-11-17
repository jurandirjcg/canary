package br.com.jgon.canary.util;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

public abstract class DateUtil {

    /**
     * 
     * extract from org.apache.http.client.utils.DateUtils
     */
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String PATTERN_RFC1036 = "EEE, dd-MMM-yy HH:mm:ss zzz";
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
    /*
     * 
     */
    public static final String PATTERN_DD_MM_YYYY_BAR = "dd/MM/yyyy";
    public static final String PATTERN_DD_MM_YYYY_HH_MM_BAR = "dd/MM/yyyy HH:mm";
    public static final String PATTERN_DD_MM_YYYY_HH_MM_SS_BAR = "dd/MM/yyyy HH:mm:ss";
    public static final String PATTERN_DD_MM_YYYY = "dd-MM-yyyy";
    public static final String PATTERN_DD_MM_YYYY_HH_MM = "dd-MM-yyyy HH:mm";
    public static final String PATTERN_DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy HH:mm:ss";
    public static final String ISO_8601_MILLIS_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String ISO_8601_EXTENDED_MILLIS_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static Date parseDate(String dateValue) throws ParseException {
        return DateUtils.parseDate(dateValue, new String[] { DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern(),
            DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern(),
            DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern(),
            DateFormatUtils.ISO_8601_EXTENDED_TIME_FORMAT.getPattern(),
            DateFormatUtils.ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT.getPattern(),
            ISO_8601_EXTENDED_MILLIS_DATETIME_FORMAT,
            ISO_8601_MILLIS_DATETIME_FORMAT,
            PATTERN_ASCTIME,
            PATTERN_RFC1036,
            PATTERN_RFC1123,
            PATTERN_DD_MM_YYYY,
            PATTERN_DD_MM_YYYY_BAR,
            PATTERN_DD_MM_YYYY_HH_MM,
            PATTERN_DD_MM_YYYY_HH_MM_BAR,
            PATTERN_DD_MM_YYYY_HH_MM_SS,
            PATTERN_DD_MM_YYYY_HH_MM_SS_BAR });
    }
}
