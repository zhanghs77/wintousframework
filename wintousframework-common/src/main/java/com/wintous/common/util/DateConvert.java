package com.ctg.itrdc.event.utils;

import org.apache.commons.beanutils.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConvert implements Converter {
    private static final Logger LOG       = LoggerFactory.getLogger(DateConvert.class);
    private static final String DATE      = "yyyy-MM-dd";
    private static final String DATETIME  = "yyyy-MM-dd HH:mm:ss";
    private static final String TIMESTAMP = "yyyy-MM-dd HH:mm:ss.SSS";
    
    @Override
    public Object convert(Class arg0, Object arg1) {
        return toDate(arg0, arg1);
    }
    
    public static Object toDate(Class type, Object value) {
        if (value == null || "".equals(value)) {
            return null;
        }
        if (value instanceof String) {
            String dateValue = value.toString().trim();
            int length = dateValue.length();
            Date date = null;
            try {
                DateFormat formatter = null;
                if (length <= 10) {
                    formatter = new SimpleDateFormat(DATE, new DateFormatSymbols(Locale.CHINA));
                    date = formatter.parse(dateValue);
                } else if (length <= 19) {
                    formatter = new SimpleDateFormat(DATETIME, new DateFormatSymbols(Locale.CHINA));
                    date = formatter.parse(dateValue);
                } else if (length <= 23) {
                    formatter = new SimpleDateFormat(TIMESTAMP, new DateFormatSymbols(Locale.CHINA));
                    date = formatter.parse(dateValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (type.equals(java.util.Date.class) && date != null) {
                return date;
            } else if (type.equals(java.sql.Date.class) && date != null) {
                return new java.sql.Date(date.getTime());
            } else if (type.equals(Timestamp.class) && date != null) {
                return new Timestamp(date.getTime());
            }
        }
        return value;
    }
}
