package com.ctg.itrdc.event.utils;

/**
 * 日志接口.
 * @author zhanghr
 *
 */
public interface ILogger {
    
    /**
     * 输出info信息.
     * @param message 
     */
    void info(String message);
    
    /**
     * 输出info信息.
     * @param message 
     * @param t
     */
    void info(String message, Throwable t);
    
    /**
     * 输出debug信息.
     * @param message
     */
    void debug(String message);
    
    /**
     * 输出debug信息. 
     * @param message
     * @param t
     */
    void debug(String message, Throwable t);
    
    /**
     * 输出warn信息.
     * @param message
     */
    void warn(String message);
    
    /**
     * 输出warn信息.
     * @param message
     * @param t
     */
    void warn(String message, Throwable t);
    
    /**
     * 输出error信息. 
     * @param message
     */
    void error(String message);
    
    /**
     * 输出error信息. 
     * @param message
     * @param t
     */
    void error(String message, Throwable t);
    
    /**
     * 输出error信息. 
     * @param format
     * @param arg
     */
    void error(String format, Object arg);
}
