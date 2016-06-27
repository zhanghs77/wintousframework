package com.ctg.itrdc.event.utils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClassInfoUtils {
    
    private static ConcurrentMap<String, Class<?>>             tableNameToClassMap = new ConcurrentHashMap<String, Class<?>>();
    
    private static ConcurrentMap<Class<?>, List<EntityColumn>> columnMap           = new ConcurrentHashMap<Class<?>, List<EntityColumn>>();
    
    /**
     * 
     * 方法功能:
     *  获取Select字段.
     * @param tableName
     * @param alias
     * @return
     * @author: linzq
     * @修改记录： 
     * ==============================================================<br>
     * 日期:2014-12-7 linzq 创建方法，并实现其功能
     * ==============================================================<br>
     */
    public static String getSelectColumns(String tableName, String alias) {
        if (!tableNameToClassMap.containsKey(tableName)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        List<EntityColumn> list = columnMap.get(tableNameToClassMap.get(tableName));
        int i = 0;
        for (EntityColumn column : list) {
            if (i++ != 0) {
                sb.append(',');
            }
            if (!StringUtils.isNullOrEmpty(alias)) {
                sb.append(alias + "." + column.getColumnName());
            } else {
                sb.append(column.getColumnName());
            }
        }
        sb.append(" ");
        return sb.toString();
    }
}
