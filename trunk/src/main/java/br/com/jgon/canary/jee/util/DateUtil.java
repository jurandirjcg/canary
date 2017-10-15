package br.com.jgon.canary.jee.util;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import br.com.jgon.canary.jee.exception.ApplicationException;
import br.com.jgon.canary.jee.exception.MessageSeverity;

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
    
	public static Date parseDate(String dateValue) throws ApplicationException{
		try {
			return DateUtils.parseDate(dateValue, new String[]{DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern(), 
					DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern(),
					DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern(),
					DateFormatUtils.ISO_8601_EXTENDED_TIME_FORMAT.getPattern(),
					DateFormatUtils.ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT.getPattern(),
					PATTERN_ASCTIME,
					PATTERN_RFC1036,
					PATTERN_RFC1123,
					PATTERN_DD_MM_YYYY,
					PATTERN_DD_MM_YYYY_BAR,
					PATTERN_DD_MM_YYYY_HH_MM,
					PATTERN_DD_MM_YYYY_HH_MM_BAR,
					PATTERN_DD_MM_YYYY_HH_MM_SS,
					PATTERN_DD_MM_YYYY_HH_MM_SS_BAR});
		} catch (ParseException e) {
			throw new ApplicationException(MessageSeverity.ERROR, "error.parse-date", e, dateValue);
		}
	}
}
