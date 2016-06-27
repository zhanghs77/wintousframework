package com.ctg.itrdc.event.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * 集合工具类
 * 
 * 
 */
@SuppressWarnings({"unchecked", "rawtypes" })
public class CollectionUtils {
    /**
     * 提取集合中的对象的两个属性(通过Getter函数), 组合成Map.
     * 
     * @param collection
     *            来源集合.
     * @param keyPropertyName
     *            要提取为Map中的Key值的属性名.
     * @param valuePropertyName
     *            要提取为Map中的Value值的属性名.
     */
    public static Map extractToMap(final Collection collection, final String keyPropertyName,
        final String valuePropertyName) {
        Map map = new HashMap(collection.size());
        
        try {
            for (Object obj : collection) {
                map.put(PropertyUtils.getProperty(obj, keyPropertyName),
                    PropertyUtils.getProperty(obj, valuePropertyName));
            }
        } catch (Exception e) {
            throw ReflectUtil.convertReflectionExceptionToUnchecked(e);
        }
        
        return map;
    }
    
    /**
     * 提取集合中的对象的一个属性(通过Getter函数), 组合成List.
     * 
     * @param collection
     *            来源集合.
     * @param propertyName
     *            要提取的属性名.
     */
    public static List extractToList(final Collection collection, final String propertyName) {
        List list = new ArrayList(collection.size());
        
        try {
            for (Object obj : collection) {
                list.add(PropertyUtils.getProperty(obj, propertyName));
            }
        } catch (Exception e) {
            throw ReflectUtil.convertReflectionExceptionToUnchecked(e);
        }
        
        return list;
    }
    
    /**
     * 提取集合中的对象的一个属性(通过Getter函数), 组合成由分割符分隔的字符串.
     * 
     * @param collection
     *            来源集合.
     * @param propertyName
     *            要提取的属性名.
     * @param separator
     *            分隔符.
     */
    public static String extractToString(final Collection collection, final String propertyName,
        final String separator) {
        List list = extractToList(collection, propertyName);
        return StringUtils.join(list, separator);
    }
    
    /**
     * 转换Collection所有元素(通过toString())为String, 中间以 separator分隔。
     */
    public static String convertToString(final Collection collection, final String separator) {
        return StringUtils.join(collection, separator);
    }
    
    /**
     * 转换Collection所有元素(通过toString())为String,
     * 每个元素的前面加入prefix，后面加入postfix，如<div>mymessage</div>。
     */
    public static String convertToString(final Collection collection, final String prefix,
        final String postfix) {
        StringBuilder builder = new StringBuilder();
        for (Object o : collection) {
            builder.append(prefix).append(o).append(postfix);
        }
        return builder.toString();
    }
    
    /**
     * 获取Collection的最后一个元素 ，如果collection为空返回null.
     */
    public static <T> T getLast(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        
        // 当类型为List时，直接取得最后一个元素 。
        if (collection instanceof List) {
            List<T> list = (List<T>) collection;
            return list.get(list.size() - 1);
        }
        
        // 其他类型通过iterator滚动到最后一个元素.
        Iterator<T> iterator = collection.iterator();
        while (true) {
            T current = iterator.next();
            if (!iterator.hasNext()) {
                return current;
            }
        }
    }
    
    /**
     * 返回a+b的新List.
     */
    public static <T> List<T> union(final Collection<T> a, final Collection<T> b) {
        List<T> result = new ArrayList<T>(a);
        result.addAll(b);
        return result;
    }
    
    /**
     * 返回a-b的新List.
     */
    public static <T> List<T> subtract(final Collection<T> a, final Collection<T> b) {
        List<T> list = new ArrayList<T>(a);
        for (T element : b) {
            list.remove(element);
        }
        
        return list;
    }
    
    /**
     * 返回a与b的交集的新List.
     */
    public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
        List<T> list = new ArrayList<T>();
        
        for (T element : a) {
            if (b.contains(element)) {
                list.add(element);
            }
        }
        return list;
    }
    
    /**
     * 判断一个集合对象是否为null或为空
     * 
     * @param collection
     *            要检测的集合对象
     * @return 若集合对象为null或为空，则返回true；否则返回false
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    /**
     * 判断一个集合对象是否不为null且不为空
     * 
     * @param collection
     *            要检测的集合对象
     * @return 若集合对象不为null且不为空，则返回true；否则返回false
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !CollectionUtils.isEmpty(collection);
    }
    
    /**
     * 得到集合的第一个元素
     * 
     * @param <T>
     *            泛型
     * @param collection
     *            集合
     * @return 集合的第一个元素，若集合为null，则返回null
     */
    public static <T> T getFirst(Collection<T> collection) {
        return isEmpty(collection) ? null
            : collection.iterator().next();
    }
    
    /**
     * 得到集合的第一个元素
     * 
     * @param <T>
     *            泛型
     * @param collection
     *            集合
     * @return 集合的第一个元素，若集合为null，则返回null
     */
    public static <T> T getFirst(List<T> list) {
        return isEmpty(list) ? null
            : list.get(0);
    }
    
    public static <T> T[] getArrayPropertiesFromListObjs(List list, String property)
        throws Exception {
        
        if (property != null && property.length() >= 2) {
            if (!Character.isUpperCase(property.charAt(1))) {// 第二个字符没有大写
                property = StringUtils.firstCharLowCase(property);
            }
        } else {
            if (property == null)
                return null;
            
            property = StringUtils.firstCharLowCase(property);
        }
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            Object firt = list.get(0);
            Object array = Array.newInstance(PropertyUtils.getProperty(firt, property).getClass(),
                list.size());
            for (int i = 0; i < list.size(); i++) {
                Object obj = list.get(i);
                T v = (T) PropertyUtils.getProperty(obj, property);
                Array.set(array, i, v);
            }
            return (T[]) ArrayUtils.toObjectArray(array);
            
        }
    }
    
    /**
     * Return <code>true</code> if the supplied Map is <code>null</code> or
     * empty. Otherwise, return <code>false</code>.
     * 
     * @param map
     *            the Map to check
     * @return whether the given Map is empty
     */
    public static boolean isEmpty(Map map) {
        return (map == null || map.isEmpty());
    }
    
    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }
    
    /**
     * Convert the supplied array into a List. A primitive array gets converted
     * into a List of the appropriate wrapper type.
     * <p>
     * A <code>null</code> source value will be converted to an empty List.
     * 
     * @param source
     *            the (potentially primitive) array
     * @return the converted List result
     * @see ObjectUtils#toObjectArray(Object)
     */
    public static List arrayToList(Object source) {
        return Arrays.asList(ObjectUtils.toObjectArray(source));
    }
    
    /**
     * Merge the given array into the given Collection.
     * 
     * @param array
     *            the array to merge (may be <code>null</code>)
     * @param collection
     *            the target Collection to merge the array into
     */
    public static void mergeArrayIntoCollection(Object array, Collection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        Object[] arr = ObjectUtils.toObjectArray(array);
        for (int i = 0; i < arr.length; i++) {
            collection.add(arr[i]);
        }
    }
    
    /**
     * 把一个map和一个给定的map合成 例如:
     * 
     * <pre>
     * Map<String,String[]> map=new HashMap<String,String[]>();
     * map.put("1",new String[]{"1"});
     * map.put("2",new String[]{"2"});	
     * Map<String,String[]> map2=new HashMap<String,String[]>();
     * map2.put("1","222");
     * map2.put("2",new String[]{"23","45"});
     * Map map3=CollectionUtils.mergeMapIntoGivenMap(map, map2);
     * map3.get("1");// "1","222"
     * map3.get("2"); // "2","23","45"
     * @param givenMap
     * @param map
     * @return
     */
    public static Map<Object, Object[]> mergeMapIntoGivenMap(Map givenMap, Map map) {
        Set keys = givenMap.keySet();
        
        Map<Object, Object[]> newMap = new HashMap<Object, Object[]>();
        for (Entry en : (Set<Entry>) givenMap.entrySet()) {
            Object value = en.getValue();
            if (value == null)
                continue;
            if (value.getClass().isArray()) {
                Object[] objs = ObjectUtils.toObjectArray(value);
                newMap.put(en.getKey(), objs);
            } else {
                Object[] newArray = (Object[]) Array.newInstance(value.getClass(), 1);
                Array.set(newArray, 0, value);
                newMap.put(en.getKey(), newArray);
            }
        }
        
        for (Entry en : (Set<Entry>) map.entrySet()) {
            Object value = en.getValue();
            if (value == null)
                continue;
            if (value.getClass().isArray()) {
                Object[] objs = ObjectUtils.toObjectArray(value);
                Object[] old = newMap.get(en.getKey());
                if (old != null) {
                    Object[] newObjs = (Object[]) ArrayUtils.append(old, objs);
                    newMap.put(en.getKey(), newObjs);
                } else {
                    newMap.put(en.getKey(), objs);
                }
                
            } else {
                Object[] newArray = (Object[]) Array.newInstance(value.getClass(), 1);
                Array.set(newArray, 0, value);
                Object[] old = newMap.get(en.getKey());
                if (old != null) {
                    Object[] newObjs = (Object[]) ArrayUtils.append(old, newArray);
                    newMap.put(en.getKey(), newObjs);
                } else {
                    newMap.put(en.getKey(), newArray);
                }
            }
        }
        
        return newMap;
    }
    
    /**
     * Merge the given Properties instance into the given Map, copying all
     * properties (key-value pairs) over.
     * <p>
     * Uses <code>Properties.propertyNames()</code> to even catch default
     * properties linked into the original Properties instance.
     * 
     * @param props
     *            the Properties instance to merge (may be <code>null</code>)
     * @param map
     *            the target Map to merge the properties into
     */
    public static void mergePropertiesIntoMap(Properties props, Map map) {
        if (map == null) {
            throw new IllegalArgumentException("Map must not be null");
        }
        if (props != null) {
            for (Enumeration en = props.propertyNames(); en.hasMoreElements();) {
                String key = (String) en.nextElement();
                map.put(key, props.getProperty(key));
            }
        }
    }
    
    /**
     * Check whether the given Iterator contains the given element.
     * 
     * @param iterator
     *            the Iterator to check
     * @param element
     *            the element to look for
     * @return <code>true</code> if found, <code>false</code> else
     */
    public static boolean contains(Iterator iterator, Object element) {
        if (iterator != null) {
            while (iterator.hasNext()) {
                Object candidate = iterator.next();
                if (ObjectUtils.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Check whether the given Enumeration contains the given element.
     * 
     * @param enumeration
     *            the Enumeration to check
     * @param element
     *            the element to look for
     * @return <code>true</code> if found, <code>false</code> else
     */
    public static boolean contains(Enumeration enumeration, Object element) {
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                Object candidate = enumeration.nextElement();
                if (ObjectUtils.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Check whether the given Collection contains the given element instance.
     * <p>
     * Enforces the given instance to be present, rather than returning
     * <code>true</code> for an equal element as well.
     * 
     * @param collection
     *            the Collection to check
     * @param element
     *            the element to look for
     * @return <code>true</code> if found, <code>false</code> else
     */
    public static boolean containsInstance(Collection collection, Object element) {
        if (collection != null) {
            for (Iterator it = collection.iterator(); it.hasNext();) {
                Object candidate = it.next();
                if (candidate == element) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Return <code>true</code> if any element in '<code>candidates</code>' is
     * contained in '<code>source</code>'; otherwise returns <code>false</code>.
     * 
     * @param source
     *            the source Collection
     * @param candidates
     *            the candidates to search for
     * @return whether any of the candidates has been found
     */
    public static boolean containsAny(Collection source, Collection candidates) {
        if (isEmpty(source) || isEmpty(candidates)) {
            return false;
        }
        for (Iterator it = candidates.iterator(); it.hasNext();) {
            if (source.contains(it.next())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Return the first element in '<code>candidates</code>' that is contained
     * in '<code>source</code>'. If no element in '<code>candidates</code>' is
     * present in '<code>source</code>' returns <code>null</code>. Iteration
     * order is {@link Collection} implementation specific.
     * 
     * @param source
     *            the source Collection
     * @param candidates
     *            the candidates to search for
     * @return the first present object, or <code>null</code> if not found
     */
    public static Object findFirstMatch(Collection source, Collection candidates) {
        if (isEmpty(source) || isEmpty(candidates)) {
            return null;
        }
        for (Iterator it = candidates.iterator(); it.hasNext();) {
            Object candidate = it.next();
            if (source.contains(candidate)) {
                return candidate;
            }
        }
        return null;
    }
    
    /**
     * Find a value of the given type in the given Collection.
     * 
     * @param collection
     *            the Collection to search
     * @param type
     *            the type to look for
     * @return a value of the given type found, or <code>null</code> if none
     * @throws IllegalArgumentException
     *             if more than one value of the given type found
     */
    public static Object findValueOfType(Collection collection, Class type)
        throws IllegalArgumentException {
        if (isEmpty(collection)) {
            return null;
        }
        Class typeToUse = (type != null ? type
            : Object.class);
        Object value = null;
        for (Iterator it = collection.iterator(); it.hasNext();) {
            Object obj = it.next();
            if (typeToUse.isInstance(obj)) {
                if (value != null) {
                    throw new IllegalArgumentException("More than one value of type ["
                        + typeToUse.getName() + "] found");
                }
                value = obj;
            }
        }
        return value;
    }
    
    /**
     * Find a value of one of the given types in the given Collection: searching
     * the Collection for a value of the first type, then searching for a value
     * of the second type, etc.
     * 
     * @param collection
     *            the collection to search
     * @param types
     *            the types to look for, in prioritized order
     * @return a of one of the given types found, or <code>null</code> if none
     * @throws IllegalArgumentException
     *             if more than one value of the given type found
     */
    public static Object findValueOfType(Collection collection, Class[] types)
        throws IllegalArgumentException {
        if (isEmpty(collection) || ObjectUtils.isEmpty(types)) {
            return null;
        }
        for (int i = 0; i < types.length; i++) {
            Object value = findValueOfType(collection, types[i]);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
    
    /**
     * Determine whether the given Collection only contains a single unique
     * object.
     * 
     * @param collection
     *            the Collection to check
     * @return <code>true</code> if the collection contains a single reference
     *         or multiple references to the same instance, <code>false</code>
     *         else
     */
    public static boolean hasUniqueObject(Collection collection) {
        if (isEmpty(collection)) {
            return false;
        }
        boolean hasCandidate = false;
        Object candidate = null;
        for (Iterator it = collection.iterator(); it.hasNext();) {
            Object elem = it.next();
            if (!hasCandidate) {
                hasCandidate = true;
                candidate = elem;
            } else if (candidate != elem) {
                return false;
            }
        }
        return true;
    }
    
    public static <T, W> Collection<W> getValues(Map<T, W> map) {
        return null;
    }
    
    public static Collection<Object> getSortedValues(Map<Integer, Object> vParameters) {
        if (vParameters == null || vParameters.size() == 0)
            return null;
        // Set<Integer> keys = vParameters.keySet();
        
        SortedMap<Integer, Object> sortedMap = new TreeMap<Integer, Object>(vParameters);
        Collection<Object> result = sortedMap.values();
        return result;
    }
    
    /**
     * 递归打印set
     * 
     * @param set
     * @return
     */
    public static String toString(Set set) {
        
        if (set == null)
            return "";
        String s = "{";
        for (Object value : set) {
            Class c = value.getClass();
            if (value instanceof Map) {
                s = s + (s.length() == 1 ? ""
                    : ",") + "{" + toString((Map) value) + "}";
            } else if (value instanceof List) {
                s = s + (s.length() == 1 ? ""
                    : ",") + "{" + toString((List) value) + "}";
            } else if (value instanceof Set) {
                s = s + (s.length() == 1 ? ""
                    : ",") + "{" + toString((Set) value) + "}";
            } else {
                
                if (c.isArray()) {
                    s = s + (s.length() == 1 ? ""
                        : ",") + "[" + ArrayUtils.toString(value, ",") + "]";
                } else {
                    s = s + (s.length() == 1 ? ""
                        : ",") + value;
                }
            }
        }
        s = s + "}";
        return s;
    }
    
    /**
     * 递归打印list
     * 
     * @param list
     * @return
     */
    public static String toString(List list) {
        
        if (list == null)
            return "";
        String s = "{";
        for (Object value : list) {
            Class c = value.getClass();
            if (value instanceof Map) {
                
                s = s + (s.length() == 1 ? ""
                    : ",") + "{" + toString((Map) value) + "}";
            } else if (value instanceof List) {
                s = s + (s.length() == 1 ? ""
                    : ",") + "{" + toString((List) value) + "}";
            } else if (value instanceof Set) {
                s = s + (s.length() == 1 ? ""
                    : ",") + "{" + toString((Set) value) + "}";
            } else {
                
                if (c.isArray()) {
                    s = s + (s.length() == 1 ? ""
                        : ",") + "[" + ArrayUtils.toString(value, ",") + "]";
                } else {
                    s = s + (s.length() == 1 ? ""
                        : ",") + "" + value;
                }
            }
            
        }
        s = s + "}";
        return s;
    }
    
    /**
     * 递归打印map
     * 
     * @param map
     * @return
     */
    public static String toString(Map map) {
        if (CollectionUtils.isEmpty(map))
            return "";
        
        Set keys = map.keySet();
        String s = "{";
        for (Object key : keys) {
            Object value = map.get(key);
            Class c = value.getClass();
            if (value instanceof Map) {
                
                s = s + (s.length() == 1 ? ""
                    : ",") + key + "={" + toString((Map) value) + "}";
            } else if (value instanceof List) {
                s = s + (s.length() == 1 ? ""
                    : ",") + key + "={" + toString((List) value) + "}";
            } else if (value instanceof Set) {
                s = s + (s.length() == 1 ? ""
                    : ",") + key + "={" + toString((Set) value) + "}";
            } else {
                
                if (c.isArray()) {
                    s = s + (s.length() == 1 ? ""
                        : ",") + key + "=[" + ArrayUtils.toString(value, ",") + "]";
                } else {
                    s = s + (s.length() == 1 ? ""
                        : ",") + key + "=" + value;
                }
            }
        }
        s = s + "}";
        return s;
    }
    
    public static <T> Map<T, T[]> putMap(Map<T, T[]> map, T key, T value) {
        
        T[] v = map.get(key);
        if (v == null) {
            v = (T[]) Array.newInstance(value.getClass(), 1);
            v[0] = value;
        } else {
            v = (T[]) ArrayUtils.append(v, value);
        }
        map.put(key, v);
        return map;
    }
    
    /**
     * 把一个集合src根据callback的映射关系转换成一个数组
     * 
     * @param <T1>
     * @param <T2>
     * @param src
     * @param callback
     * @return
     */
    public static <T1, T2> T2[] mapToArray(Collection<T1> src, Callback<T1, T2> callback) {
        List<T2> list = new ArrayList<T2>();
        for (T1 obj : src) {
            list.add(callback.call(obj));
        }
        return (T2[]) ArrayUtils.toArray(list);
        
    }
    
    /**
     * 把集合根据callback的映射关系转换成一个List
     * 
     * @param <T1>
     * @param <T2>
     * @param src
     * @param callback
     * @return
     */
    public static <T1, T2> List<T2> mapToList(Collection<T1> src, Callback<T1, T2> callback) {
        List<T2> list = new ArrayList<T2>();
        for (T1 obj : src) {
            list.add(callback.call(obj));
        }
        return list;
        
    }
    
    public static Object getMaxKeyInMap(Map map) {
        Set set = map.keySet();
        Object maxKey = Collections.max(set);
        return maxKey;
    }
    
    public static String toJavascriptString(Collection obj) throws Exception {
        
        String resultStr = "";
        if (CollectionUtils.isEmpty(obj)) {
            return "[]";
        }
        
        for (Object fvalue : obj) {
            
            if (fvalue == null)
                continue;
            if (fvalue.getClass().isArray()) {//
                resultStr = resultStr + "," + ArrayUtils.toJavascriptString(fvalue);
            } else if (fvalue instanceof Map) {
                resultStr = resultStr + MapUtils.toJavascriptString((Map) fvalue);
            } else if (fvalue instanceof Collection) { // 对象
                resultStr = resultStr + ","
                    + CollectionUtils.toJavascriptString((Collection) fvalue);
            } else {
                resultStr = resultStr + "," + ObjectUtils.toJavascriptString(fvalue);
            }
            
        }
        
        return "[" + StringUtils.trimLeadingString(resultStr, ",") + "]";
    }
    
}
