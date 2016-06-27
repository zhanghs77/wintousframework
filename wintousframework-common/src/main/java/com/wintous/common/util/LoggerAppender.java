package com.ctg.itrdc.event.utils;

/**
 * 基于slf4j接口封装.
 * 
 * @author zhanghr
 * 
 */
public class LoggerAppender implements ILogger {
    
    private org.slf4j.Logger log;
    
    public LoggerAppender(Class<?> name) {
        log = org.slf4j.LoggerFactory.getLogger(name);
    }
    
    @Override
    public void info(String message) {
        // TODO Auto-generated method stub
        log.info(message);
    }
    
    @Override
    public void info(String message, Throwable t) {
        // TODO Auto-generated method stub
        log.info(message, t);
    }
    
    @Override
    public void debug(String message) {
        // TODO Auto-generated method stub
        log.debug(message);
    }
    
    @Override
    public void debug(String message, Throwable t) {
        // TODO Auto-generated method stub
        log.debug(message, t);
    }
    
    @Override
    public void error(String message) {
        // TODO Auto-generated method stub
        log.error(message);
    }
    
    @Override
    public void error(String message, Throwable t) {
        // TODO Auto-generated method stub
        log.error(message, t);
    }
    
    @Override
    public void warn(String message) {
        // TODO Auto-generated method stub
        log.warn(message);
    }
    
    @Override
    public void warn(String message, Throwable t) {
        // TODO Auto-generated method stub
        log.warn(message, t);
    }
    
    @Override
    public void error(String format, Object arg) {
        log.error(format, arg);
    }
}
