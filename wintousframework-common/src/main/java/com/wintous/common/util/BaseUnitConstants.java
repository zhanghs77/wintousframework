package com.ctg.itrdc.event.utils;

/**
 * 基本的工具类.
 * 
 * @version Revision 1.0.0
 * @see:
 */
public class BaseUnitConstants {
    
    /**
    /**
     * 缓存key的分割符.
     */
    public static final String CACHE_TAG_SPLIT      = "_#_";
    
    /**
     * SPRING 的beanname 控制符
     */
    public final static String SPRING_CTL           = "$";
    
    /**
     * 产品新装.
     */
    public static final String PROD_ACTION_NEW      = "100";
    
    /**
     * 销售品订购.
     */
    public static final String PRODOFFER_ACTION_NEW = "500";
    
    public static final int    CACHE_DEFAULT_TTL    = 3600;
    
    /**
     * 时间类型
     * 属性规格配置
     * .
     */
    public static final String DEF_VAL_TODAY        = "$today";
    
    /**
     * 属性类型
     * attrType T3 表示枚举类型属性
     * .
     */
    public static final String SELECT_ATTR          = "T3";
    
    /**
     * RET_TRUE String.
     */
    public static final String RET_TRUE             = "TRUE";
    
    /**
     * RET_FALSE String.
     */
    public static final String RET_FALSE            = "FALSE";
    
    /**
     * service offer 的操作类型 add 新增.
     */
    public static final String SO_OP_ADD            = "10";
    /**
     * service offer 的操作类型 mod 更新.
     */
    public static final String SO_OP_MOD            = "11";
    /**
     * service offer 的操作类型 del.
     */
    public static final String SO_OP_DEL            = "12";
    
    public static final Long   CACHE_ENTT_BACKUP    = 2L;
    public static final Long   CACHE_GLOBAL         = 1L;
    public static final Long   CACHE_NONE           = 0L;
    
    public static final String SESSION              = "session";
    
    public static final String EVENT_FACTOR_KEY     = "EVENT_FACTOR";
    
}
