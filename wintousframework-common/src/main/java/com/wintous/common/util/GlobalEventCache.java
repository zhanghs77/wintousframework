package com.ctg.itrdc.event.utils;

import org.apache.commons.collections.map.LRUMap;

/**
 * 全局存储事件相关信息类.
 * @author yihe
 *
 */
public class GlobalEventCache {
    /**
     * 全局存储事件相关lrumap
     */
    static LRUMap lrumap = new LRUMap(); //org.apache.commons.collections.map
                                         
    /**
     * 设置缓存值.
     * @param key
     * @param obj
     */
    public static void setObject(String key, Object obj) {
        lrumap.put(key, obj);
    }
    
    /**
     * 获取缓存值
     * @param key
     * @return
     */
    public static Object getObject(String key) {
        return lrumap.get(key);
    }
    
}
