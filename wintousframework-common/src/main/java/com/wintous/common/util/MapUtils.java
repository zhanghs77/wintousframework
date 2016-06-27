package com.ctg.itrdc.event.utils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
 
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
 
import java.util.Map.Entry;
 

import org.apache.commons.beanutils.ConvertUtils;

/**
 * Map工具类<br>
 * 
 * 
 * 
 */
public class MapUtils extends org.apache.commons.collections.MapUtils {
    static {
        ConvertUtils.register(new DateConvert(), Date.class);
        ConvertUtils.register(new DateConvert(), java.sql.Date.class);
        ConvertUtils.register(new DateConvert(), Timestamp.class);
    }
    
    /**
     * 将Map转换为Object
     * 
     * @param clazz
     *            目标对象的类
     * @param map
     *            待转换Map
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public static <T, V> T toObject(Class<T> clazz, Map<String, V> map)
        throws InstantiationException, IllegalAccessException, InvocationTargetException {
        T object = clazz.newInstance();
        return toObject(object, map);
    }
    
    /**
     * 将Map转换为Object
     * 
     * @param clazz
     *            目标对象的类
     * @param map
     *            待转换Map
     * @param toCamelCase
     *            是否去掉下划线
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public static <T, V> T toObject(Class<T> clazz, Map<String, V> map, boolean toCamelCase)
        throws InstantiationException, IllegalAccessException, InvocationTargetException {
        T object = clazz.newInstance();
        return toObject(object, map, toCamelCase);
    }
    
    /**
     * 将Map转换为Object
     * 
     * @param object
     *            目标对象
     * @param map
     *            待转换Map
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public static <T, V> T toObject(T object, Map<String, V> map)
        throws InstantiationException, IllegalAccessException, InvocationTargetException {
        return toObject(object, map, false);
    }
    
    public static <T, V> T toObject(T object, Map<String, V> map, boolean toCamelCase)
        throws InstantiationException, IllegalAccessException, InvocationTargetException {
        if (toCamelCase)
            map = toCamelCaseMap(map);
        BeanUtils.populate(object, map);
        return object;
    }
    
    /**
     * 对象转Map
     * 
     * @param object
     *            目标对象
     * @return
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     * @throws NoSuchMethodException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> toMap(Object object)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtils.describe(object);
    }
    
    /**
     * 转换为Collection<Map<K, V>>
     * 
     * @param collection
     *            待转换对象集合
     * @return 转换后的Collection<Map<K, V>>
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     * @throws NoSuchMethodException
     */
    public static <T> Collection<Map<String, String>> toMapList(Collection<T> collection)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
        if (collection != null && !collection.isEmpty()) {
            for (Object object : collection) {
                Map<String, String> map = toMap(object);
                retList.add(map);
            }
        }
        return retList;
    }
    
    /**
     * 转换为Collection,同时为字段做驼峰转换<Map<K, V>>
     * 
     * @param collection
     *            待转换对象集合
     * @return 转换后的Collection<Map<K, V>>
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     * @throws NoSuchMethodException
     */
    public static <T> Collection<Map<String, String>> toMapListForFlat(Collection<T> collection)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
        if (collection != null && !collection.isEmpty()) {
            for (Object object : collection) {
                Map<String, String> map = toMapForFlat(object);
                retList.add(map);
            }
        }
        return retList;
    }
    
    /**
     * 转换成Map并提供字段命名驼峰转平行
     * 
     * @param clazz
     *            目标对象所在类
     * @param object
     *            目标对象
     * @param map
     *            待转换Map
     * @throws NoSuchMethodException
     * @throws java.lang.reflect.InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Map<String, String> toMapForFlat(Object object)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Map<String, String> map = toMap(object);
        return toUnderlineStringMap(map);
    }
    
    /**
     * 将Map的Keys去下划线<br>
     * (例:branch_no -> branchNo )<br>
     * 
     * @param map
     *            待转换Map
     * @return
     */
    public static <V> Map<String, V> toCamelCaseMap(Map<String, V> map) {
        Map<String, V> newMap = new HashMap<String, V>();
        for (Entry<String, V> entry : map.entrySet()) {
            safeAddToMap(newMap, JavaBeanUtil.toCamelCaseString(entry.getKey()), entry.getValue());
        }
        return newMap;
    }
    
    /**
     * 将Map的Keys转译成下划线格式的<br>
     * (例:branchNo -> branch_no)<br>
     * 
     * @param map
     *            待转换Map
     * @return
     */
    public static <V> Map<String, V> toUnderlineStringMap(Map<String, V> map) {
        Map<String, V> newMap = new HashMap<String, V>();
        for (Entry<String, V> entry : map.entrySet()) {
            newMap.put(JavaBeanUtil.toUnderlineString(entry.getKey()), entry.getValue());
        }
        return newMap;
    }
    
    public static <T> Map<T, T[]> putMap(Map<T, T[]> map, T key, T value) {
        
        return CollectionUtils.putMap(map, key, value);
    }
    
    public static <T1, T2> T2[] mapToArray(Collection<T1> src, Callback<T1, T2> callback) {
        return CollectionUtils.mapToArray(src, callback);
        
    }
    
    public static <T1, T2> List<T2> mapToList(Collection<T1> src, Callback<T1, T2> callback) {
        return CollectionUtils.mapToList(src, callback);
    }
    
    public static Object getMaxKeyInMap(Map map) {
        return CollectionUtils.getMaxKeyInMap(map);
    }
    
    public static void mergePropertiesIntoMap(Properties props, Map map) {
        
        CollectionUtils.mergePropertiesIntoMap(props, map);
    }
    
    public static Map<Object, Object[]> mergeMapIntoGivenMap(Map givenMap, Map map) {
        
        return CollectionUtils.mergeMapIntoGivenMap(givenMap, map);
    }
    
    public static String toJavascriptString(Map map) throws Exception {
        
        String resultStr = "";
        if (MapUtils.isEmpty(map)) {
            return "{}";
        }
        
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry en = (Entry) it.next();
            if (en.getKey() == null)
                continue;
            Object fvalue = en.getValue();
            if (fvalue == null)
                continue;
            if (fvalue.getClass().isArray()) {//
                resultStr = resultStr + ",\"" + en.getKey() + "\":" + ArrayUtils.toJavascriptString(fvalue);
            } else if (fvalue instanceof Map) {
                resultStr = resultStr + ",\"" + en.getKey() + "\":"
                    + MapUtils.toJavascriptString((Map) fvalue);
            } else if (fvalue instanceof Collection) { // 对象
                resultStr = resultStr + ",\"" + en.getKey() + "\":"
                    + CollectionUtils.toJavascriptString((Collection) fvalue);
            } else {
                resultStr = resultStr + ",\"" + en.getKey() + "\":"
                    + ObjectUtils.toJavascriptString(fvalue);
            }
        }
        return "{" + StringUtils.trimLeadingString(resultStr, ",") + "}";
        
    }
    
}
