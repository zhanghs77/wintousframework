package com.ctg.itrdc.event.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.cglib.beans.BeanCopier;

/**
 * 扩展org.apache.commons.beanutils.BeanUtils<br>
 * 
 * @author Wesley<br>
 * @修改时间：2014-11-19
 * @修改人：wangfeihu
 * @修改内容：增加 copy方法
 * 
 * @version 1.2
 * 
 */
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {
    
    /**
     * 复制列表
     * 
     * @param sourceList
     * @param destClass
     * @return
     * @throws Exception
     */
    @SuppressWarnings({"rawtypes", "unchecked" })
    public static <T> List<T> copyList(List sourceList, Class<T> destClass) throws Exception {
        List<T> list = new ArrayList<T>();
        if (sourceList != null && sourceList.size() > 0) {
            for (Object obj : sourceList) {
                Object dest = destClass.newInstance();
                applyIf(dest, obj);
                list.add((T) dest);
            }
        }
        return list;
    }
    
    /**
     * 将源对象中的值覆盖到目标对象中，仅覆盖源对象中不为NULL值的属性
     * 
     * @param dest
     *            目标对象，标准的JavaBean
     * @param orig
     *            源对象，可为Map、标准的JavaBean
     * @throws BusinessException
     */
    public static void applyIf(Object dest, Object orig) throws Exception {
        applyIf(dest, orig, true);
    }
    
    /**
     * 拷贝对象
     * @param dest
     * @param orig
     * @param isOnlyCopyNotNull  是否只拷贝非空属性
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static void applyIf(Object dest, Object orig, boolean isOnlyCopyNotNull)
        throws Exception {
        if (!isOnlyCopyNotNull) {
            copyBeanByPropertyUtils(dest, orig);
        } else {
            if (orig instanceof Map) {
                Iterator names = ((Map) orig).keySet().iterator();
                while (names.hasNext()) {
                    String name = (String) names.next();
                    if (PropertyUtils.isWriteable(dest, name)) {
                        Object value = ((Map) orig).get(name);
                        if (value != null) {
                            PropertyUtils.setNestedProperty(dest, name, value);
                        }
                    }
                }
            } else {
                // 使用有问题:long不能转换为String，Long不能转换为long,Long不能转换为int；
                // 比如实体属性orderItemId为long类型，DTO中属性orderItemId为String类型，
                // 转换报错，本地封装的copy方法。
                // 报错信息：org.springframework.beans.FatalBeanException: Could not copy
                // properties from source to target; nested exception is
                // java.lang.IllegalArgumentException: argument type mismatch
                BeanUtils.copyBeanbyRflect(dest, orig);
            }
        }
    }
    
    private static void copyBeanbyRflect(Object dest, Object orig) {
        org.springframework.beans.BeanUtils.copyProperties(orig, dest);
        //        try {
        //            copy(dest, orig);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
    }
    
    /****************
     * 通过cglib的BeanCopier.copy将源对象复制到目标对象中，支持父类属性复制。 TODO :
     * 当源对象的get/set方法不匹配的时候，拷贝失败
     * 
     * @param dest
     * @param orig
     * @throws Exception
     */
    @SuppressWarnings("unused")
    private void copyBeanbyDymProxy(Object dest, Object orig) throws Exception {
        BeanCopier beanCopier = BeanCopier.create(orig.getClass(), dest.getClass(), false);
        beanCopier.copy(orig, dest, null);
    }
    
    /***************
     * 通过PropertyUtils工具将源对象中的数据复制到目标对象中(反射机制). 效率相对于Spring BeanUtils低
     * 拷贝不为空的字段
     * @param fields
     *            复制属性字段
     * @param dest
     *            目标对象
     * @param orig
     *            源对象
     * @throws Exception
     */
    private static void copyBeanByPropertyUtils(Object dest, Object orig) throws Exception {
        if (dest == null) {
            throw new RtManagerException("dest 为空");
        }
        List<Class<?>> clazzs = ClassUtils.getAllSuperclasses(dest.getClass());
        if (clazzs == null) {
            clazzs = new ArrayList<Class<?>>();
        }
        clazzs.add(dest.getClass());
        if (clazzs != null && clazzs.size() > 0) {
            for (Class<?> clazz : clazzs) {
                Field[] fields = clazz.getDeclaredFields();
                if (fields != null && fields.length > 0) {
                    for (int i = 0; i < fields.length; i++) {
                        String name = fields[i].getName();
                        if (PropertyUtils.isReadable(orig, name)
                            && PropertyUtils.isWriteable(dest, name)) {
                            Object value = PropertyUtils.getNestedProperty(orig, name);
                            
                            if (value != null) {
                                PropertyUtils.setNestedProperty(dest, name, value);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 将源对象中的值覆盖到目标对象中，仅覆盖源对象中不为NULL值的属性
     * 
     * @param orig
     *            源对象，标准的JavaBean
     * @param dest
     *            排除检查的属性，Map
     * 
     * @throws BusinessException
     */
    @SuppressWarnings("rawtypes")
    public static boolean checkObjProperty(Object orig, Map dest) throws Exception {
        try {
            java.lang.reflect.Field[] fields = orig.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                String name = fields[i].getName();
                if (!dest.containsKey(name)) {
                    if (PropertyUtils.isReadable(orig, name)) {
                        Object value = PropertyUtils.getSimpleProperty(orig, name);
                        if (value == null) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            throw new Exception("将源对象中的值覆盖到目标对象中，仅覆盖源对象中不为NULL值的属性", e);
        }
    }
    
    //    /**
    //     * 两个对象之间的值拷贝, 可以自动转换数据类型
    //     * 
    //     * @param dest
    //     * @param orig
    //     * @throws Exception
    //     */
    //    public static void copy(Object dest, Object orig) throws Exception {
    //        try {
    //            if (dest == null || orig == null) {
    //                return;
    //            }
    //            java.lang.reflect.Field[] sfields = dest.getClass().getDeclaredFields();
    //            java.lang.reflect.Field[] superFields = dest.getClass().getSuperclass()
    //                .getDeclaredFields();
    //            java.lang.reflect.Field[] fields = new Field[sfields.length + superFields.length];
    //            for (int i = 0; i < sfields.length; i++) {
    //                fields[i] = sfields[i];
    //            }
    //            for (int i = 0; i < superFields.length; i++) {
    //                fields[sfields.length + i] = superFields[i];
    //            }
    //            
    //            for (int i = 0; i < fields.length; i++) {
    //                String name = fields[i].getName();
    //                if (name.startsWith("serial")) {
    //                    continue;
    //                }
    //                if (PropertyUtils.isReadable(orig, name) && PropertyUtils.isWriteable(dest, name)) {
    //                    Object value = PropertyUtils.getSimpleProperty(orig, name);
    //                    if (value != null) {
    //                        Field field = null;
    //                        try {
    //                            field = fields[i];
    //                            Class cla = field.getType();
    //                            if (cla.equals(String.class)) {
    //                                PropertyUtils.setSimpleProperty(dest, name, value.toString());
    //                            } else if (cla.equals(Long.class)) {
    //                                PropertyUtils.setSimpleProperty(dest, name,
    //                                    Long.valueOf(value.toString()));
    //                            } else if (cla.equals(Integer.class)) {
    //                                PropertyUtils.setSimpleProperty(dest, name,
    //                                    Integer.valueOf(value.toString()));
    //                            } else if (cla.equals(Date.class)) {
    //                                Date date = convertStringToDate(value.toString());
    //                                PropertyUtils.setSimpleProperty(dest, name, date);
    //                            } else if (cla.equals(Timestamp.class)) {
    //                                Date date = convertStringToDate(value.toString());
    //                                Timestamp time = new Timestamp(date.getTime());
    //                                PropertyUtils.setSimpleProperty(dest, name, time);
    //                            } else if (cla.equals(long.class)) {
    //                                PropertyUtils.setSimpleProperty(dest, name,
    //                                    Long.valueOf(value.toString()));
    //                            } else if (cla.equals(int.class)) {
    //                                PropertyUtils.setSimpleProperty(dest, name,
    //                                    Integer.valueOf(value.toString()));
    //                            }
    //                        } catch (Exception e) {
    //                            e.printStackTrace();
    //                            field = null;
    //                        }
    //                    }
    //                } else {
    //                    // System.out.println(dest.getClass().getSimpleName() +
    //                    // "----------" + name);
    //                }
    //            }
    //            
    //        } catch (Exception e) {
    //            throw new Exception("将源对象中的值覆盖到目标对象中，仅覆盖源对象中不为NULL值的属性", e);
    //        }
    //    }
    
    /**
     * 转换一个时间字符串To Date 对象 , 内置多套转换格式
     * 
     * @param dateString
     * @return
     */
    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat df = new SimpleDateFormat();
        List<String> fmts = new ArrayList<String>();
        fmts.add("yyyy-MM-dd HH:mm:ss");
        fmts.add("yyyy-MM-dd HH:mm:ss.SSS");
        fmts.add("yyyy-MM-dd hh:mm:ss");
        fmts.add("yyyyMMddhhmmss");
        fmts.add("yyyy-MM-dd");
        fmts.add("yy/MM/dd");
        fmts.add("yyyyMMdd");
        Date date = null;
        for (int i = 0; i < fmts.size(); i++) {
            df.applyPattern(fmts.get(i));
            try {
                date = df.parse(dateString);
                break;
            } catch (ParseException e) {
                
            }
        }
        if (date == null) {
            try {
                long timeMill = Long.valueOf(dateString.trim()).longValue();
                date = new Date();
                date.setTime(timeMill);
            } catch (NumberFormatException e) {
                date = null;
                // e.printStackTrace();
            }
        }
        return date;
    }
    
    /**
     * 是否是虚拟主键.
     * @param text
     * @return
     * @author Luxb
     * 2014年11月27日 Luxb
     */
    public static boolean isVirtualKey(String text) {
        return text.trim().matches("\\$-[\\d]+\\$");
    }
    
    /**
     * 设置属性值
     * 
     * @param javaBean
     *            javaBean对象
     * @param pd
     *            javaBean属性
     */
    public static void setPropertyValue(Object javaBean, PropertyDescriptor pd,
            Object value) {
        try {
            if (value == null) {
                return;
            }

            // 通过setter保存
            Method method = pd.getWriteMethod();
            if (method == null) {
                return;
            }

            Class<?> cls = null;
            Class<?>[] clss = method.getParameterTypes();
            if (clss != null && clss.length == 1) {
                cls = clss[0];
            }

            if (cls == null) {
                return;
            }

            if (!cls.isPrimitive()) {
                method.invoke(javaBean, value);
            } else if (long.class == cls) {
                method.invoke(javaBean, ((Long) value).longValue());
            } else if (int.class == cls) {
                method.invoke(javaBean, ((Integer) value).intValue());
            } else if (boolean.class == cls) {
                method.invoke(javaBean, ((Boolean) value).booleanValue());
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("通过setter保存字段值失败！", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("通过setter保存字段值失败！", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("通过setter保存字段值失败！", e);
        } catch (Throwable t) {
            throw new RuntimeException("通过setter保存字段值失败！", t);
        }
    }
    
}
