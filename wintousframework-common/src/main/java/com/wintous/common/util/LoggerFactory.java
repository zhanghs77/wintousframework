package com.ctg.itrdc.event.utils;

/**
 * 日志工厂类.
 * @author zhanghr
 *
 */
public class LoggerFactory {

    private LoggerFactory() {
    }
    
    public static ILogger getLogger(Class<?> name) {
        return new LoggerAppender(name);
    }
}
