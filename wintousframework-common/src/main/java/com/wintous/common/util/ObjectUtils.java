package com.ctg.itrdc.event.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Object类型转换工具
 * 
 * 
 */
public class ObjectUtils {
    public static Set<Class<?>>  simpleType              = new HashSet<Class<?>>();
    
    static {
        simpleType.add(boolean.class);
        simpleType.add(Boolean.class);
        simpleType.add(String.class);
        simpleType.add(char.class);
        simpleType.add(Character.class);
        simpleType.add(byte.class);
        simpleType.add(Byte.class);
        simpleType.add(Integer.class);
        simpleType.add(int.class);
        simpleType.add(Long.class);
        simpleType.add(long.class);
        simpleType.add(Short.class);
        simpleType.add(short.class);
        simpleType.add(Float.class);
        simpleType.add(float.class);
        simpleType.add(Double.class);
        simpleType.add(double.class);
        simpleType.add(java.util.Date.class);
    }
    private static final ILogger log                     = LoggerFactory
                                                             .getLogger(ObjectUtils.class);
    private static final int     INITIAL_HASH            = 7;
    private static final int     MULTIPLIER              = 31;
    
    private static final String  EMPTY_STRING            = "";
    private static final String  NULL_STRING             = "null";
    private static final String  ARRAY_START             = "{";
    private static final String  ARRAY_END               = "}";
    private static final String  EMPTY_ARRAY             = ARRAY_START + ARRAY_END;
    private static final String  ARRAY_ELEMENT_SEPARATOR = ", ";
    
    /**
     * Return whether the given throwable is a checked exception: that is,
     * neither a RuntimeException nor an Error.
     * 
     * @param ex
     *            the throwable to check
     * @return whether the throwable is a checked exception
     * @see java.lang.Exception
     * @see java.lang.RuntimeException
     * @see java.lang.Error
     */
    public static boolean isCheckedException(Throwable ex) {
        return !(ex instanceof RuntimeException || ex instanceof Error);
    }
    
    /**
     * 
     * @param o
     *            要序列化的对象，必须遵循java序列化对象的要求,即对象是可序列化的
     * @return 对象对象的字节数组
     * @throws Exception
     */
    public static byte[] serializeObject(Object o) throws Exception {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        if (o == null) {
            return null;
        }
        
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.flush();
            return bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
        }
    }
    
    /**
     * 
     * @param data
     *            对象序列化字节流
     * @return 反序列化的对象
     * @throws Exception
     */
    public static Object deSerializeObject(byte[] data) throws Exception {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        if (data == null || data.length == 0) {
            return null;
        }
        try {
            bis = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(bis);
            return ois.readObject();
        } finally {
            if (ois != null) {
                ois.close();
            }
        }
    }
    
    public static boolean checkedSimpleType(Class<?> t) {
        if (simpleType.contains(t)) {
            return true;
        }
        return false;
    }
    
    /**
     * Check whether the given exception is compatible with the exceptions
     * declared in a throws clause.
     * 
     * @param ex
     *            the exception to checked
     * @param declaredExceptions
     *            the exceptions declared in the throws clause
     * @return whether the given exception is compatible
     */
    @SuppressWarnings({"rawtypes", "unchecked" })
    public static boolean isCompatibleWithThrowsClause(Throwable ex, Class[] declaredExceptions) {
        if (!isCheckedException(ex)) {
            return true;
        }
        if (declaredExceptions != null) {
            for (int i = 0; i < declaredExceptions.length; i++) {
                if (declaredExceptions[i].isAssignableFrom(ex.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Return whether the given array is empty: that is, <code>null</code> or of
     * zero length.
     * 
     * @param array
     *            the array to check
     * @return whether the given array is empty
     */
    public static boolean isEmpty(Object[] array) {
        return (array == null || array.length == 0);
    }
    
    /**
     * Append the given Object to the given array, returning a new array
     * consisting of the input array contents plus the given Object.
     * 
     * @param array
     *            the array to append to (can be <code>null</code>)
     * @param obj
     *            the Object to append
     * @return the new array (of the same component type; never
     *         <code>null</code>)
     */
    public static Object[] addObjectToArray(Object[] array, Object obj) {
        Class<?> compType = Object.class;
        if (array != null) {
            compType = array.getClass().getComponentType();
        } else if (obj != null) {
            compType = obj.getClass();
        }
        int newArrLength = (array != null ? array.length + 1
            : 1);
        Object[] newArr = (Object[]) Array.newInstance(compType, newArrLength);
        if (array != null) {
            System.arraycopy(array, 0, newArr, 0, array.length);
        }
        newArr[newArr.length - 1] = obj;
        return newArr;
    }
    
    /**
     * Convert the given array (which may be a primitive array) to an object
     * array (if necessary of primitive wrapper objects).
     * <p>
     * A <code>null</code> source value will be converted to an empty Object
     * array.
     * 
     * @param source
     *            the (potentially primitive) array
     * @return the corresponding object array (never <code>null</code>)
     * @throws IllegalArgumentException
     *             if the parameter is not an array
     */
    public static Object[] toObjectArray(Object source) {
        if (source instanceof Object[]) {
            return (Object[]) source;
        }
        if (source == null) {
            return new Object[0];
        }
        if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: " + source);
        }
        
        int length = Array.getLength(source);
        if (length == 0) {
            return new Object[0];
        }
        Class<?> wrapperType = Array.get(source, 0).getClass();
        Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
        for (int i = 0; i < length; i++) {
            newArray[i] = Array.get(source, i);
        }
        return newArray;
    }
    
    // ---------------------------------------------------------------------
    // Convenience methods for content-based equality/hash-code handling
    // ---------------------------------------------------------------------
    
    /**
     * Determine if the given objects are equal, returning <code>true</code> if
     * both are <code>null</code> or <code>false</code> if only one is
     * <code>null</code>.
     * <p>
     * Compares arrays with <code>Arrays.equals</code>, performing an equality
     * check based on the array elements rather than the array reference.
     * 
     * @param o1
     *            first Object to compare
     * @param o2
     *            second Object to compare
     * @return whether the given objects are equal
     * @see java.util.Arrays#equals
     */
    public static boolean nullSafeEquals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }
        if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
        if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        }
        if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        }
        if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        }
        if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        }
        if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        }
        if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        }
        if (o1 instanceof short[] && o2 instanceof short[]) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        }
        return false;
    }
    
    /**
     * Return as hash code for the given object; typically the value of
     * <code>{@link Object#hashCode()}</code>. If the object is an array, this
     * method will delegate to any of the <code>nullSafeHashCode</code> methods
     * for arrays in this class. If the object is <code>null</code>, this method
     * returns 0.
     * 
     * @see #nullSafeHashCode(Object[])
     * @see #nullSafeHashCode(boolean[])
     * @see #nullSafeHashCode(byte[])
     * @see #nullSafeHashCode(char[])
     * @see #nullSafeHashCode(double[])
     * @see #nullSafeHashCode(float[])
     * @see #nullSafeHashCode(int[])
     * @see #nullSafeHashCode(long[])
     * @see #nullSafeHashCode(short[])
     */
    public static int nullSafeHashCode(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Object[]) {
            return nullSafeHashCode((Object[]) obj);
        }
        if (obj instanceof boolean[]) {
            return nullSafeHashCode((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return nullSafeHashCode((byte[]) obj);
        }
        if (obj instanceof char[]) {
            return nullSafeHashCode((char[]) obj);
        }
        if (obj instanceof double[]) {
            return nullSafeHashCode((double[]) obj);
        }
        if (obj instanceof float[]) {
            return nullSafeHashCode((float[]) obj);
        }
        if (obj instanceof int[]) {
            return nullSafeHashCode((int[]) obj);
        }
        if (obj instanceof long[]) {
            return nullSafeHashCode((long[]) obj);
        }
        if (obj instanceof short[]) {
            return nullSafeHashCode((short[]) obj);
        }
        return obj.hashCode();
    }
    
    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     */
    public static int nullSafeHashCode(Object[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + nullSafeHashCode(array[i]);
        }
        return hash;
    }
    
    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     */
    public static int nullSafeHashCode(boolean[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + hashCode(array[i]);
        }
        return hash;
    }
    
    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     */
    public static int nullSafeHashCode(byte[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + array[i];
        }
        return hash;
    }
    
    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     */
    public static int nullSafeHashCode(char[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + array[i];
        }
        return hash;
    }
    
    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     */
    public static int nullSafeHashCode(double[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + hashCode(array[i]);
        }
        return hash;
    }
    
    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     */
    public static int nullSafeHashCode(float[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + hashCode(array[i]);
        }
        return hash;
    }
    
    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     */
    public static int nullSafeHashCode(int[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + array[i];
        }
        return hash;
    }
    
    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     */
    public static int nullSafeHashCode(long[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + hashCode(array[i]);
        }
        return hash;
    }
    
    /**
     * Return a hash code based on the contents of the specified array. If
     * <code>array</code> is <code>null</code>, this method returns 0.
     */
    public static int nullSafeHashCode(short[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = MULTIPLIER * hash + array[i];
        }
        return hash;
    }
    
    /**
     * Return the same value as <code>{@link Boolean#hashCode()}</code>.
     * 
     * @see Boolean#hashCode()
     */
    public static int hashCode(boolean bool) {
        return bool ? 1231
            : 1237;
    }
    
    /**
     * Return the same value as <code>{@link Double#hashCode()}</code>.
     * 
     * @see Double#hashCode()
     */
    public static int hashCode(double dbl) {
        long bits = Double.doubleToLongBits(dbl);
        return hashCode(bits);
    }
    
    /**
     * Return the same value as <code>{@link Float#hashCode()}</code>.
     * 
     * @see Float#hashCode()
     */
    public static int hashCode(float flt) {
        return Float.floatToIntBits(flt);
    }
    
    /**
     * Return the same value as <code>{@link Long#hashCode()}</code>.
     * 
     * @see Long#hashCode()
     */
    public static int hashCode(long lng) {
        return (int) (lng ^ (lng >>> 32));
    }
    
    // ---------------------------------------------------------------------
    // Convenience methods for toString output
    // ---------------------------------------------------------------------
    
    /**
     * Return a String representation of an object's overall identity.
     * 
     * @param obj
     *            the object (may be <code>null</code>)
     * @return the object's identity as String representation, or
     *         <code>null</code> if the object was <code>null</code>
     */
    public static String identityToString(Object obj) {
        if (obj == null) {
            return EMPTY_STRING;
        }
        return obj.getClass().getName() + "@" + getIdentityHexString(obj);
    }
    
    /**
     * Return a hex String form of an object's identity hash code.
     * 
     * @param obj
     *            the object
     * @return the object's identity code in hex notation
     */
    public static String getIdentityHexString(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }
    
    /**
     * Return a content-based String representation if <code>obj</code> is not
     * <code>null</code>; otherwise returns an empty String.
     * <p>
     * Differs from {@link #nullSafeToString(Object)} in that it returns an
     * empty String rather than "null" for a <code>null</code> value.
     * 
     * @param obj
     *            the object to build a display String for
     * @return a display String representation of <code>obj</code>
     * @see #nullSafeToString(Object)
     */
    public static String getDisplayString(Object obj) {
        if (obj == null) {
            return EMPTY_STRING;
        }
        return nullSafeToString(obj);
    }
    
    /**
     * Determine the class name for the given object.
     * <p>
     * Returns <code>"null"</code> if <code>obj</code> is <code>null</code>.
     * 
     * @param obj
     *            the object to introspect (may be <code>null</code>)
     * @return the corresponding class name
     */
    public static String nullSafeClassName(Object obj) {
        return (obj != null ? obj.getClass().getName()
            : NULL_STRING);
    }
    
    /**
     * Return a String representation of the specified Object.
     * <p>
     * Builds a String representation of the contents in case of an array.
     * Returns <code>"null"</code> if <code>obj</code> is <code>null</code>.
     * 
     * @param obj
     *            the object to build a String representation for
     * @return a String representation of <code>obj</code>
     */
    public static String nullSafeToString(Object obj) {
        if (obj == null) {
            return NULL_STRING;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Object[]) {
            return nullSafeToString((Object[]) obj);
        }
        if (obj instanceof boolean[]) {
            return nullSafeToString((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return nullSafeToString((byte[]) obj);
        }
        if (obj instanceof char[]) {
            return nullSafeToString((char[]) obj);
        }
        if (obj instanceof double[]) {
            return nullSafeToString((double[]) obj);
        }
        if (obj instanceof float[]) {
            return nullSafeToString((float[]) obj);
        }
        if (obj instanceof int[]) {
            return nullSafeToString((int[]) obj);
        }
        if (obj instanceof long[]) {
            return nullSafeToString((long[]) obj);
        }
        if (obj instanceof short[]) {
            return nullSafeToString((short[]) obj);
        }
        String str = obj.toString();
        return str;
    }
    
    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     * 
     * @param array
     *            the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(Object[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            buffer.append(String.valueOf(array[i]));
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }
    
    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     * 
     * @param array
     *            the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(boolean[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            
            buffer.append(array[i]);
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }
    
    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     * 
     * @param array
     *            the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(byte[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            buffer.append(array[i]);
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }
    
    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     * 
     * @param array
     *            the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(char[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            buffer.append("'").append(array[i]).append("'");
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }
    
    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     * 
     * @param array
     *            the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(double[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            
            buffer.append(array[i]);
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }
    
    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     * 
     * @param array
     *            the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(float[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            
            buffer.append(array[i]);
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }
    
    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     * 
     * @param array
     *            the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(int[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            buffer.append(array[i]);
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }
    
    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     * 
     * @param array
     *            the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(long[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            buffer.append(array[i]);
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }
    
    /**
     * Return a String representation of the contents of the specified array.
     * <p>
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
     * separated by the characters <code>", "</code> (a comma followed by a
     * space). Returns <code>"null"</code> if <code>array</code> is
     * <code>null</code>.
     * 
     * @param array
     *            the array to build a String representation for
     * @return a String representation of <code>array</code>
     */
    public static String nullSafeToString(short[] array) {
        if (array == null) {
            return NULL_STRING;
        }
        int length = array.length;
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append(ARRAY_START);
            } else {
                buffer.append(ARRAY_ELEMENT_SEPARATOR);
            }
            buffer.append(array[i]);
        }
        buffer.append(ARRAY_END);
        return buffer.toString();
    }
    
    //
    // /**
    // * 把java对象转换成一个map对象
    // *
    // * @param obj
    // * @return
    // */
    // public static Map<String, Object> fromJavaBeanToMap(Object obj) {
    // ObjectMapper mapper = new ObjectMapper();
    // Map<String, Object> fieldMap = mapper.convertValue(obj, Map.class);
    // return fieldMap;
    // }
    //
    //
    //
    
    public static boolean isPrimitiveWapper(Class<?> t) {
        if (t == Integer.class || t == Boolean.class || t == Long.class
        
        || t == Short.class || t == Float.class || t == Double.class || t == Byte.class
            || t == Character.class) {
            return true;
        }
        return false;
        
    }
    
    @SuppressWarnings({"rawtypes", "unchecked" })
    public static <T> T fromMapToJavaBean(T javaBean, Map fromMap) {
        try {
            T bean = javaBean;
            // Map<String, Object> nestedObjs = new HashMap<String,
            // Object>();
            Map<?, ?> map = PropertyUtils.describe(bean);
            Set<?> set = map.keySet();
            Iterator<?> i = set.iterator();
            Map newFromMap = new HashMap();
            Set fromMapKeys = fromMap.keySet();
            for (Object key : fromMapKeys) {
                String key2 = StringUtils.firstCharLowCase(key.toString());
                newFromMap.put(key2, fromMap.get(key));
            }
            
            while (i.hasNext()) {
                
                String name = (String) i.next();
                if (name.equals("class"))
                    continue;
                
                Class<?> t = PropertyUtils.getPropertyType(bean, name);
                Object obj = newFromMap.get(name);
                // name为javabean属性名
                // 简单类型
                
                if (obj != null) {
                    if (obj.getClass().isArray()) {
                        
                        if (!t.isArray()) {
                            
                            Object objval = null;
                            if (Array.getLength(obj) > 0) {
                                objval = Array.get(obj, 0);
                                if (objval instanceof String) {
                                    Object des = convertStringToTargetClassObject(objval + "", t);
                                    PropertyUtils.setProperty(bean, name, des);
                                    
                                } else {
                                    if (t == String.class) {
                                        if (objval != null) {
                                            PropertyUtils
                                                .setProperty(bean, name, objval.toString());
                                        } else {
                                            //                                            PropertyUtils.setProperty(bean, name, objval);
                                            PropertyUtils.setProperty(bean, name, "");
                                        }
                                    } else {
                                        PropertyUtils.setProperty(bean, name, t.cast(objval));
                                    }
                                    
                                }
                            } else {
                                PropertyUtils.setProperty(bean, name, objval);
                            }
                        } else {
                            Class ct = t.getComponentType();
                            Object arrays = Array.newInstance(ct, Array.getLength(obj));
                            for (int m = 0; m < Array.getLength(obj); m++) {
                                Object v = Array.get(obj, m);
                                if (v instanceof String) {
                                    Object des = convertStringToTargetClassObject(v + "", ct);
                                    Array.set(arrays, m, des);
                                    
                                } else {
                                    if (ct == String.class) {
                                        if (v != null) {
                                            Array.set(arrays, m, v.toString());
                                        } else {
                                            //                                            Array.set(arrays, m, v);
                                            Array.set(arrays, m, "");
                                        }
                                        
                                    } else {
                                        Array.set(arrays, m, v);
                                    }
                                    
                                }
                            }
                            PropertyUtils.setProperty(bean, name, arrays);
                            
                        }
                        
                    } else {
                        // NumberUtils.convertNumberToTargetClass(number,
                        if (!t.isArray()) {
                            // targetClass)
                            // Object objval = obj;
                            if (obj instanceof String) {
                                String objStr = null;
                                objStr = obj.toString();
                                Object des = convertStringToTargetClassObject(objStr, t);
                                PropertyUtils.setProperty(bean, name, des);
                                
                            } else {
                                PropertyUtils.setProperty(bean, name, t.cast(obj));
                            }
                        } else {
                            Class ct = t.getComponentType();
                            Object arrays = Array.newInstance(ct, 1);
                            
                            Object v = obj;
                            if (v instanceof String) {
                                Object des = convertStringToTargetClassObject(v + "", ct);
                                Array.set(arrays, 0, des);
                                
                            } else {
                                if (ct == String.class) {
                                    Array.set(arrays, 0, v.toString());
                                } else {
                                    Array.set(arrays, 0, v);
                                }
                                
                            }
                            
                            PropertyUtils.setProperty(bean, name, arrays);
                            
                        }
                    }
                } else {
                    PropertyUtils.setProperty(bean, name, null);
                }
                
            }// while
            
            return bean;
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }
    
    public static Object convertStringToTargetClassObject(String src, Class<?> targetClass) {
        if (ObjectUtils.checkedSimpleType(targetClass)) {
            if (targetClass == String.class) {
                return src;
            } else if (targetClass == char.class || targetClass == Character.class) {
                if (src.length() >= 1)
                    return src.charAt(0);
            } else if (targetClass == java.util.Date.class) {
                try {
                    if (StringUtils.hasText(src))
                        return DateUtils.convertStrToDate(src);
                } catch (Exception e) {
                    return null;
                }
            } else {
                if (StringUtils.hasText(src) && Number.class.isAssignableFrom(targetClass)) {
                    return NumberUtils.parseNumber(src.trim(), targetClass);
                } else {
                    return null;
                }
            }
        }
        return null;
        
    }
    
    public static Map<String, Object> getMapFromResultSet(ResultSet rs) {
        
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            
            ResultSetMetaData rsMeta = rs.getMetaData();
            
            for (int i = 0; i < rsMeta.getColumnCount(); i++) {
                String columnName = rsMeta.getColumnLabel(i + 1);
                log.debug("columnName=" + columnName + ",val=" + rs.getObject(columnName));
                
                map.put(columnName, rs.getObject(columnName));
            }
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("", e);
        }
        return map;
        
    }
    
    /**
     * 把一个JavaBean转换成一个javabean，第一个JavaBean里的所有和第二个JavaBean相同名称
     * 属性都会赋予第二个JavaBean的相应属性. 注意：属性类型只能是简单类型才能赋值
     * 
     * @param <T>
     * @param clazz
     * @param rs
     * @return
     */
    public static <T1, T2> T1 getBeanFromBean(Class<T1> clazz, T2 fromBean) {
        
        try {
            
            T1 bean = clazz.newInstance();
            // Map<String, Object> nestedObjs = new HashMap<String,
            // Object>();
            // Map<?, ?> map = PropertyUtils.describe(bean);
            // Set<?> set = map.keySet();
            // Iterator<?> i = set.iterator();
            PropertyUtils.copyProperties(bean, fromBean);
            return bean;
            
        } catch (Exception e) {
            log.error("", e);
        }
        
        return null;
        
    }
    
    public static <T1, T2> T1 fromBeanToBean(Class<T1> clazz, T2 fromBean) {
        return getBeanFromBean(clazz, fromBean);
    }
    
    public static String toJavascriptString(Object obj) throws Exception {
        
        String resultStr = "";
        if (obj == null)
            return "{}";
        if (obj.getClass().isPrimitive() || isPrimitiveWapper(obj.getClass())) {
            return resultStr = resultStr + obj;
        } else if (obj.getClass() == String.class) {
            if (((String) obj).startsWith("[javascript]")) {
                return resultStr = StringUtils.trimLeadingString(obj + "", "[javascript]");
            } else {
                return resultStr = resultStr + "\"" + obj + "\"";
            }
        } else if (obj.getClass().isArray()) {
            return ArrayUtils.toJavascriptString(obj);
        } else if (obj instanceof Map) {
            return MapUtils.toJavascriptString((Map<?, ?>) obj);
        } else if (obj instanceof Collection) {
            return CollectionUtils.toJavascriptString((Collection<?>) obj);
        }
        
        Class<?> c = obj.getClass();
        
        Field[] fields = c.getDeclaredFields();
        Map<String, Object> fieldsNameValues = new HashMap<String, Object>();
        Map<String, Field> fieldsNameField = new HashMap<String, Field>();
        String[] values = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod))
                continue;
            
            values[i] = field.getName();
            fieldsNameValues.put(field.getName(), field.get(obj));
            fieldsNameField.put(field.getName(), field);
        }
        try {
            
            for (int i = 0; i < values.length; i++) {
                String fieldName = values[i];
                
                Field field = fieldsNameField.get(fieldName);
                
                Object fvalue = field.get(obj);
                if (fvalue == null) {
                    continue;
                }
                
                Class<?> fieldType = field.getType();
                
                if (fieldType != null) {
                    // 基本类型
                    if (fieldType.isPrimitive() || isPrimitiveWapper(fieldType)
                        || fieldType == String.class) {
                        
                        resultStr = resultStr + ",\"" + fieldName + "\":"
                            + ObjectUtils.toJavascriptString(field.get(obj));
                        
                    } else if (fieldType.isArray()) {// 数组
                    
                        resultStr = resultStr + "," + ArrayUtils.toJavascriptString(field.get(obj));
                        
                    } else if (fvalue instanceof Map) {
                        resultStr = resultStr + ","
                            + MapUtils.toJavascriptString((Map<?, ?>) field.get(obj));
                        
                    } else if (fvalue instanceof Collection) {
                        
                        resultStr = resultStr + ","
                            + CollectionUtils.toJavascriptString((Collection<?>) field.get(obj));
                    } else {
                        resultStr = resultStr + "," + ObjectUtils.toJavascriptString(obj);
                    }
                } else {
                    throw new Exception("java bean里的" + StringUtils.strnull(fieldName) + "属性的类型"
                        + "" + "不支持！");
                    //                        + (fieldType != null ? fieldType.getName()
                    //                            : "") + "不支持！");
                }
                
            }
        } catch (Exception e) {
            throw new Exception("seq属性配置不正确或者配置的javabean与二进制流格式不匹配导致解析出错");
        }
        
        return "{" + StringUtils.trimLeadingString(resultStr, ",") + "}";
    }
    
    /**
     * 转换为Date类型
     * 
     * @param obj
     *            输入对象
     * @return Date
     */
    public static Date toDate(Object obj) {
        if (obj == null) {
            return null;// null默认返回值
        }
        
        if (obj instanceof Date) {
            return (Date) obj;
        }
        
        // 尝试 YYYYMMDDHH24MISS格式转换
        if (obj instanceof String) {
            return DateUtils.str2Date((String) obj);
        }
        
        // 不支持的类型转换
        return null;
    }
    
    /**
     * 转为整型
     * 
     * @param obj
     *            输入对象
     * @return int整型，如果obj不是整型默认返回0
     */
    public static int toInt(Object obj) {
        return toInt(obj, 0);
    }
    
    /**
     * 转为整型
     * 
     * @param obj
     *            输入对象
     * @param defaultValue
     *            默认值
     * @return int整型，如果obj不是整型默认返回默认值
     */
    public static int toInt(Object obj, int defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        return NumberUtils.toInt(obj.toString(), defaultValue);
    }
    
    /**
     * 转为long
     * 
     * @param obj
     *            输入对象
     * @return long整型，如果obj不是整型默认返回0
     */
    public static long toLong(Object obj) {
        return toLong(obj, 0);
    }
    
    /**
     * 转为long
     * 
     * @param obj
     *            输入对象
     * @param defaultValue
     *            默认值
     * @return long整型，如果obj不是整型默认返回默认值
     */
    public static long toLong(Object obj, long defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        return NumberUtils.toLong(obj.toString(), defaultValue);
    }
    
    /**
     * 转为float
     * 
     * @param obj
     *            输入对象
     * @return double，如果obj不是数值默认返回0
     */
    public static float toFloat(Object obj) {
        return toFloat(obj, 0);
    }
    
    /**
     * 转为float
     * 
     * @param obj
     *            输入对象
     * @param defaultValue
     *            默认值
     * @return double，如果obj不是数值默认返回默认值
     */
    public static float toFloat(Object obj, float defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        if (obj instanceof Number) {
            return ((Number) obj).floatValue();
        }
        return NumberUtils.toFloat(obj.toString(), defaultValue);
    }
    
    /**
     * 转为double
     * 
     * @param obj
     *            输入对象
     * @return double，如果obj不是数值默认返回0
     */
    public static double toDouble(Object obj) {
        return toDouble(obj, 0);
    }
    
    /**
     * 转为double
     * 
     * @param obj
     *            输入对象
     * @param defaultValue
     *            默认值
     * @return double，如果obj不是数值默认返回默认值
     */
    public static double toDouble(Object obj, double defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        return NumberUtils.toDouble(obj.toString(), defaultValue);
    }
    
    /**
     * 转为String
     * 
     * @param obj
     *            输入对象
     * @return 字符串
     */
    public static String toString(Object obj) {
        return toString(obj, StringUtils.EMPTY);
    }
    
    /**
     * 转为String
     * 
     * @param obj
     * @param defaultValue
     *            默认字符串
     * @return 如果obj是null返回默认字符串
     */
    public static String toString(Object obj, String defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        
        String value = null;
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Date) {
            value = DateUtils.date2Str((Date) obj);
        } else {
            value = obj.toString();
        }
        
        return value == null ? defaultValue
            : value;
    }
    
}
