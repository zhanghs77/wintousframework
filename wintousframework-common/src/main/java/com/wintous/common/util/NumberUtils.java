/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ctg.itrdc.event.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * <p>
 * Provides extra functionality for Java Number classes.
 * </p>
 * 
 * @author Apache Software Foundation
 * @author <a href="mailto:rand_mcneely@yahoo.com">Rand McNeely</a>
 * @author <a href="mailto:steve.downey@netfolio.com">Steve Downey</a>
 * @author Eric Pugh
 * @author Phil Steitz
 * @author Matthew Hawthorne
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @author <a href="mailto:fredrik@westermarck.com">Fredrik Westermarck</a>
 * @since 2.0
 * @version $Id: NumberUtils.java 1056853 2011-01-09 01:07:04Z niallp $
 */
public abstract class NumberUtils {
    
    private static final ILogger LOG               = LoggerFactory.getLogger(NumberUtils.class);
    
    /** Reusable Long constant for zero. */
    public static final Long     LONG_ZERO         = Long.valueOf(0L);
    /** Reusable Long constant for one. */
    public static final Long     LONG_ONE          = Long.valueOf(1L);
    /** Reusable Long constant for minus one. */
    public static final Long     LONG_MINUS_ONE    = Long.valueOf(-1L);
    /** Reusable Integer constant for zero. */
    public static final Integer  INTEGER_ZERO      = Integer.valueOf(0);
    /** Reusable Integer constant for one. */
    public static final Integer  INTEGER_ONE       = Integer.valueOf(1);
    /** Reusable Integer constant for minus one. */
    public static final Integer  INTEGER_MINUS_ONE = Integer.valueOf(-1);
    /** Reusable Short constant for zero. */
    public static final Short    SHORT_ZERO        = Short.valueOf((short) 0);
    /** Reusable Short constant for one. */
    public static final Short    SHORT_ONE         = Short.valueOf((short) 1);
    /** Reusable Short constant for minus one. */
    public static final Short    SHORT_MINUS_ONE   = Short.valueOf((short) -1);
    /** Reusable Byte constant for zero. */
    public static final Byte     BYTE_ZERO         = Byte.valueOf((byte) 0);
    /** Reusable Byte constant for one. */
    public static final Byte     BYTE_ONE          = Byte.valueOf((byte) 1);
    /** Reusable Byte constant for minus one. */
    public static final Byte     BYTE_MINUS_ONE    = Byte.valueOf((byte) -1);
    /** Reusable Double constant for zero. */
    public static final Double   DOUBLE_ZERO       = new Double(0.0d);
    /** Reusable Double constant for one. */
    public static final Double   DOUBLE_ONE        = new Double(1.0d);
    /** Reusable Double constant for minus one. */
    public static final Double   DOUBLE_MINUS_ONE  = new Double(-1.0d);
    /** Reusable Float constant for zero. */
    public static final Float    FLOAT_ZERO        = new Float(0.0f);
    /** Reusable Float constant for one. */
    public static final Float    FLOAT_ONE         = new Float(1.0f);
    /** Reusable Float constant for minus one. */
    public static final Float    FLOAT_MINUS_ONE   = new Float(-1.0f);
    
    // 默认除法运算精度
    private static final int     DEF_DIV_SCALE     = 10;
    
    // 字母随机数组，去除了i，o等歧义字母
    static final String[]        LETTERS           = {"a", "b", "c", "d", "e", "f", "g", "h", "j",
            "k", "m", "n", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y" };
    // 数字随机数组，去除了0和1等歧义数字
    static final String[]        NUMBERS           = {"2", "3", "4", "5", "6", "7", "8", "9" };
    
    /**
     * NumberUtils instances should NOT be constructed in standard programming.
     * Instead, the class should be used as
     * <code>NumberUtils.stringToInt("6");</code>. This constructor is public to
     * permit tools that require a JavaBean instance to operate.
     */
    public NumberUtils() {
    }
    
    // --------------------------------------------------------------------
    
    /**
     * Convert a String to an int, returning zero if the conversion fails
     * 
     * @param str
     *            the string to convert
     * @return the int represented by the string, or zero if conversion fails
     */
    public static int stringToInt(String str) {
        return stringToInt(str, 0);
    }
    
    /**
     * Convert a String to an int, returning a default value if the conversion
     * fails.
     * 
     * @param str
     *            the string to convert
     * @param defaultValue
     *            the default value
     * @return the int represented by the string, or the default if conversion
     *         fails
     */
    public static int stringToInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
    
    // --------------------------------------------------------------------
    
    // must handle Long, Float, Integer, Float, Short,
    // BigDecimal, BigInteger and Byte
    // useful methods:
    // Byte.decode(String)
    // Byte.valueOf(String,int radix)
    // Byte.valueOf(String)
    // Double.valueOf(String)
    // Float.valueOf(String)
    // new Float(String)
    // Integer.valueOf(String,int radix)
    // Integer.valueOf(String)
    // Integer.decode(String)
    // Integer.getInteger(String)
    // Integer.getInteger(String,int val)
    // Integer.getInteger(String,Integer val)
    // new Integer(String)
    // new Double(String)
    // new Byte(String)
    // new Long(String)
    // Long.getLong(String)
    // Long.getLong(String,int)
    // Long.getLong(String,Integer)
    // Long.valueOf(String,int)
    // Long.valueOf(String)
    // new Short(String)
    // Short.decode(String)
    // Short.valueOf(String,int)
    // Short.valueOf(String)
    // new BigDecimal(String)
    // new BigInteger(String)
    // new BigInteger(String,int radix)
    // Possible inputs:
    // 45 45.5 45E7 4.5E7 Hex Oct Binary xxxF xxxD xxxf xxxd
    // plus minus everything. Prolly more. A lot are not separable.
    
    /**
     * <p>
     * Turns a string value into a java.lang.Number. First, the value is
     * examined for a type qualifier on the end (
     * <code>'f','F','d','D','l','L'</code>). If it is found, it starts trying
     * to create succissively larger types from the type specified until one is
     * found that can hold the value.
     * </p>
     * <p>
     * If a type specifier is not found, it will check for a decimal point and
     * then try successively larger types from Integer to BigInteger and from
     * Float to BigDecimal.
     * </p>
     * <p>
     * If the string starts with "0x" or "-0x", it will be interpreted as a
     * hexadecimal integer. Values with leading 0's will not be interpreted as
     * octal.
     * </p>
     * 
     * @param val
     *            String containing a number
     * @return Number created from the string
     * @throws NumberFormatException
     *             if the value cannot be converted
     */
    public static Number createNumber(String val) throws NumberFormatException {
        if (val == null) {
            return null;
        }
        if (val.length() == 0) {
            throw new NumberFormatException("\"\" is not a valid number.");
        }
        if (val.startsWith("--")) {
            // this is protection for poorness in java.lang.BigDecimal.
            // it accepts this as a legal value, but it does not appear
            // to be in specification of class. OS X Java parses it to
            // a wrong value.
            return null;
        }
        if (val.startsWith("0x") || val.startsWith("-0x")) {
            return createInteger(val);
        }
        char lastChar = val.charAt(val.length() - 1);
        String mant;
        String dec;
        String exp;
        int decPos = val.indexOf('.');
        int expPos = val.indexOf('e') + val.indexOf('E') + 1;
        
        if (decPos > -1) {
            
            if (expPos > -1) {
                if (expPos < decPos) {
                    throw new NumberFormatException(val + " is not a valid number.");
                }
                dec = val.substring(decPos + 1, expPos);
            } else {
                dec = val.substring(decPos + 1);
            }
            mant = val.substring(0, decPos);
        } else {
            if (expPos > -1) {
                mant = val.substring(0, expPos);
            } else {
                mant = val;
            }
            dec = null;
        }
        if (!Character.isDigit(lastChar)) {
            if (expPos > -1 && expPos < val.length() - 1) {
                exp = val.substring(expPos + 1, val.length() - 1);
            } else {
                exp = null;
            }
            // Requesting a specific type..
            String numeric = val.substring(0, val.length() - 1);
            boolean allZeros = isAllZeros(mant) && isAllZeros(exp);
            switch (lastChar) {
                case 'l':
                case 'L':
                    if (dec == null && exp == null && isDigits(numeric.substring(1))
                        && (numeric.charAt(0) == '-' || Character.isDigit(numeric.charAt(0)))) {
                        try {
                            return createLong(numeric);
                        } catch (NumberFormatException nfe) {
                            // Too big for a long
                        }
                        return createBigInteger(numeric);
                        
                    }
                    throw new NumberFormatException(val + " is not a valid number.");
                case 'f':
                case 'F':
                    try {
                        Float f = NumberUtils.createFloat(numeric);
                        if (!(f.isInfinite() || (f.floatValue() == 0.0F && !allZeros))) {
                            // If it's too big for a float or the float value = 0
                            // and the string
                            // has non-zeros in it, then float doens't have the
                            // presision we want
                            return f;
                        }
                        
                    } catch (NumberFormatException nfe) {
                    }
                    // Fall through
                case 'd':
                case 'D':
                    try {
                        Double d = NumberUtils.createDouble(numeric);
                        if (!(d.isInfinite() || (d.floatValue() == 0.0D && !allZeros))) {
                            return d;
                        }
                    } catch (NumberFormatException nfe) {
                    }
                    try {
                        return createBigDecimal(numeric);
                    } catch (NumberFormatException e) {
                    }
                    // Fall through
                default:
                    throw new NumberFormatException(val + " is not a valid number.");
                    
            }
        } else {
            // User doesn't have a preference on the return type, so let's start
            // small and go from there...
            if (expPos > -1 && expPos < val.length() - 1) {
                exp = val.substring(expPos + 1, val.length());
            } else {
                exp = null;
            }
            if (dec == null && exp == null) {
                // Must be an int,long,bigint
                try {
                    return createInteger(val);
                } catch (NumberFormatException nfe) {
                }
                try {
                    return createLong(val);
                } catch (NumberFormatException nfe) {
                }
                return createBigInteger(val);
                
            } else {
                // Must be a float,double,BigDec
                boolean allZeros = isAllZeros(mant) && isAllZeros(exp);
                try {
                    Float f = createFloat(val);
                    if (!(f.isInfinite() || (f.floatValue() == 0.0F && !allZeros))) {
                        return f;
                    }
                } catch (NumberFormatException nfe) {
                }
                try {
                    Double d = createDouble(val);
                    if (!(d.isInfinite() || (d.doubleValue() == 0.0D && !allZeros))) {
                        return d;
                    }
                } catch (NumberFormatException nfe) {
                }
                
                return createBigDecimal(val);
                
            }
            
        }
    }
    
    /**
     * Utility method for createNumber. Returns true if s is null
     * 
     * @param s
     *            the String to check
     * @return if it is all zeros or null
     */
    private static boolean isAllZeros(String s) {
        if (s == null) {
            return true;
        }
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) != '0') {
                return false;
            }
        }
        return s.length() > 0;
    }
    
    // --------------------------------------------------------------------
    
    /**
     * Convert a String to a Float
     * 
     * @param val
     *            a String to convert
     * @return converted Float
     * @throws NumberFormatException
     *             if the value cannot be converted
     */
    public static Float createFloat(String val) {
        return Float.valueOf(val);
    }
    
    /**
     * Convert a String to a Double
     * 
     * @param val
     *            a String to convert
     * @return converted Double
     * @throws NumberFormatException
     *             if the value cannot be converted
     */
    public static Double createDouble(String val) {
        return Double.valueOf(val);
    }
    
    /**
     * Convert a String to a Integer, handling hex and octal notations.
     * 
     * @param val
     *            a String to convert
     * @return converted Integer
     * @throws NumberFormatException
     *             if the value cannot be converted
     */
    public static Integer createInteger(String val) {
        // decode() handles 0xAABD and 0777 (hex and octal) as well.
        return Integer.decode(val);
    }
    
    /**
     * Convert a String to a Long
     * 
     * @param val
     *            a String to convert
     * @return converted Long
     * @throws NumberFormatException
     *             if the value cannot be converted
     */
    public static Long createLong(String val) {
        return Long.valueOf(val);
    }
    
    /**
     * Convert a String to a BigInteger
     * 
     * @param val
     *            a String to convert
     * @return converted BigInteger
     * @throws NumberFormatException
     *             if the value cannot be converted
     */
    public static BigInteger createBigInteger(String val) {
        BigInteger bi = new BigInteger(val);
        return bi;
    }
    
    /**
     * Convert a String to a BigDecimal
     * 
     * @param val
     *            a String to convert
     * @return converted BigDecimal
     * @throws NumberFormatException
     *             if the value cannot be converted
     */
    public static BigDecimal createBigDecimal(String val) {
        BigDecimal bd = new BigDecimal(val);
        return bd;
    }
    
    // --------------------------------------------------------------------
    
    /**
     * Gets the minimum of three long values.
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the largest of the values
     */
    public static long minimum(long a, long b, long c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            a = c;
        }
        return a;
    }
    
    /**
     * Gets the minimum of three int values.
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the largest of the values
     */
    public static int minimum(int a, int b, int c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            a = c;
        }
        return a;
    }
    
    /**
     * Gets the maximum of three long values.
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the largest of the values
     */
    public static long maximum(long a, long b, long c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            a = c;
        }
        return a;
    }
    
    /**
     * Gets the maximum of three int values.
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the largest of the values
     */
    public static int maximum(int a, int b, int c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            a = c;
        }
        return a;
    }
    
    // --------------------------------------------------------------------
    
    /**
     * Compares two doubles for order.
     * <p>
     * This method is more comprhensive than the standard Java greater than,
     * less than and equals operators. It returns -1 if the first value is less
     * than the second. It returns +1 if the first value is greater than the
     * second. It returns 0 if the values are equal.
     * <p>
     * The ordering is as follows, largest to smallest:
     * <ul>
     * <li>NaN
     * <li>Positive infinity
     * <li>Maximum double
     * <li>Normal positve numbers
     * <li>+0.0
     * <li>-0.0
     * <li>Normal negative numbers
     * <li>Minimum double (-Double.MAX_VALUE)
     * <li>Negative infinity
     * </ul>
     * Comparing NaN with NaN will return 0.
     * 
     * @param lhs
     *            the first double
     * @param rhs
     *            the second double
     * @return -1 if lhs is less, +1 if greater, 0 if equal to rhs
     */
    public static int compare(double lhs, double rhs) {
        if (lhs < rhs) {
            return -1;
        }
        if (lhs > rhs) {
            return +1;
        }
        // Need to compare bits to handle 0.0 == -0.0 being true
        // compare should put -0.0 < +0.0
        // Two NaNs are also == for compare purposes
        // where NaN == NaN is false
        long lhsBits = Double.doubleToLongBits(lhs);
        long rhsBits = Double.doubleToLongBits(rhs);
        if (lhsBits == rhsBits) {
            return 0;
        }
        // Something exotic! A comparison to NaN or 0.0 vs -0.0
        // Fortunately NaN's long is > than everything else
        // Also negzeros bits < poszero
        // NAN: 9221120237041090560
        // MAX: 9218868437227405311
        // NEGZERO: -9223372036854775808
        if (lhsBits < rhsBits) {
            return -1;
        } else {
            return +1;
        }
    }
    
    /**
     * Compares two floats for order.
     * <p>
     * This method is more comprhensive than the standard Java greater than,
     * less than and equals operators. It returns -1 if the first value is less
     * than the second. It returns +1 if the first value is greater than the
     * second. It returns 0 if the values are equal.
     * <p>
     * The ordering is as follows, largest to smallest:
     * <ul>
     * <li>NaN
     * <li>Positive infinity
     * <li>Maximum float
     * <li>Normal positve numbers
     * <li>+0.0
     * <li>-0.0
     * <li>Normal negative numbers
     * <li>Minimum float (-Float.MAX_VALUE)
     * <li>Negative infinity
     * </ul>
     * Comparing NaN with NaN will return 0.
     * 
     * @param lhs
     *            the first float
     * @param rhs
     *            the second float
     * @return -1 if lhs is less, +1 if greater, 0 if equal to rhs
     */
    public static int compare(float lhs, float rhs) {
        if (lhs < rhs) {
            return -1;
        }
        if (lhs > rhs) {
            return +1;
        }
        // Need to compare bits to handle 0.0 == -0.0 being true
        // compare should put -0.0 < +0.0
        // Two NaNs are also == for compare purposes
        // where NaN == NaN is false
        int lhsBits = Float.floatToIntBits(lhs);
        int rhsBits = Float.floatToIntBits(rhs);
        if (lhsBits == rhsBits) {
            return 0;
        }
        // Something exotic! A comparison to NaN or 0.0 vs -0.0
        // Fortunately NaN's int is > than everything else
        // Also negzeros bits < poszero
        // NAN: 2143289344
        // MAX: 2139095039
        // NEGZERO: -2147483648
        if (lhsBits < rhsBits) {
            return -1;
        } else {
            return +1;
        }
    }
    
    // --------------------------------------------------------------------
    
    /**
     * Checks whether the String contains only digit characters. Null and blank
     * string will return false.
     * 
     * @param str
     *            the string to check
     * @return boolean contains only unicode numeric
     */
    public static boolean isDigits(String str) {
        if ((str == null) || (str.length() == 0)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * <p>
     * Checks whether the String a valid Java number. Valid numbers include
     * hexadecimal marked with the "0x" qualifier, scientific notation and
     * numbers marked with a type qualifier (e.g. 123L).
     * </p>
     * <p>
     * Null and blank string will return false.
     * </p>
     * 
     * @param str
     *            the string to check
     * @return true if the string is a correctly formatted number
     */
    public static boolean isNumber(String str) {
        if ((str == null) || (str.length() == 0)) {
            return false;
        }
        char[] chars = str.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // Deal with any possible sign up front
        int start = (chars[0] == '-') ? 1
            : 0;
        if (sz > start + 1) {
            if (chars[start] == '0' && chars[start + 1] == 'x') {
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // Checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f')
                        && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // Don't want to loop to the last char, check it afterwords
              // for type qualifiers
        int i = start;
        // Loop to the next to last char or to the last char if we need another
        // digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;
                
            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // Two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // We've already taken care of hex.
                if (hasExp) {
                    // Two E's
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // We need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // No type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // Can't have an E at the last byte
                return false;
            }
            if (!allowSigns
                && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l' || chars[i] == 'L') {
                // Not allowing L with an exponoent
                return foundDigit && !hasExp;
            }
        }
        // allowSigns is true iff the val ends in 'E'
        // Found digit it to make sure weird stuff like '.' and '1E-' doesn't
        // pass
        return !allowSigns && foundDigit;
    }
    
    /**
     * Convert the given number into an instance of the given target class.
     * 
     * @param number
     *            the number to convert
     * @param targetClass
     *            the target class to convert to
     * @return the converted number
     * @throws IllegalArgumentException
     *             if the target class is not supported (i.e. not a standard
     *             Number subclass as included in the JDK)
     * @see java.lang.Byte
     * @see java.lang.Short
     * @see java.lang.Integer
     * @see java.lang.Long
     * @see java.math.BigInteger
     * @see java.lang.Float
     * @see java.lang.Double
     * @see java.math.BigDecimal
     */
    public static Number convertNumberToTargetClass(Number number, Class<?> targetClass)
        throws IllegalArgumentException {
        
        Assert.notNull(number, "Number must not be null");
        Assert.notNull(targetClass, "Target class must not be null");
        
        if (targetClass.isInstance(number)) {
            return number;
        } else if (targetClass.equals(Byte.class)) {
            long value = number.longValue();
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return Byte.valueOf(number.byteValue());
        } else if (targetClass.equals(Short.class)) {
            long value = number.longValue();
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return Short.valueOf(number.shortValue());
        } else if (targetClass.equals(Integer.class)) {
            long value = number.longValue();
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return Integer.valueOf(number.intValue());
        } else if (targetClass.equals(Long.class)) {
            return Long.valueOf(number.longValue());
        } else if (targetClass.equals(Float.class)) {
            return new Float(number.floatValue());
        } else if (targetClass.equals(Double.class)) {
            return new Double(number.doubleValue());
        } else if (targetClass.equals(BigInteger.class)) {
            return BigInteger.valueOf(number.longValue());
        } else if (targetClass.equals(BigDecimal.class)) {
            // using BigDecimal(String) here, to avoid unpredictability of
            // BigDecimal(double)
            // (see BigDecimal javadoc for details)
            return new BigDecimal(number.toString());
        } else {
            throw new IllegalArgumentException("Could not convert number [" + number
                + "] of type [" + number.getClass().getName() + "] to unknown target class ["
                + targetClass.getName() + "]");
        }
    }
    
    /**
     * Raise an overflow exception for the given number and target class.
     * 
     * @param number
     *            the number we tried to convert
     * @param targetClass
     *            the target class we tried to convert to
     */
    private static void raiseOverflowException(Number number, Class<?> targetClass) {
        throw new IllegalArgumentException("Could not convert number [" + number + "] of type ["
            + number.getClass().getName() + "] to target class [" + targetClass.getName()
            + "]: overflow");
    }
    
    /**
     * Parse the given text into a number instance of the given target class,
     * using the corresponding default <code>decode</code> methods. Trims the
     * input <code>String</code> before attempting to parse the number. Supports
     * numbers in hex format (with leading 0x) and in octal format (with leading
     * 0).
     * 
     * @param text
     *            the text to convert
     * @param targetClass
     *            the target class to parse into
     * @return the parsed number
     * @throws IllegalArgumentException
     *             if the target class is not supported (i.e. not a standard
     *             Number subclass as included in the JDK)
     * @see java.lang.Byte#decode
     * @see java.lang.Short#decode
     * @see java.lang.Integer#decode
     * @see java.lang.Long#decode
     * @see #decodeBigInteger(String)
     * @see java.lang.Float#valueOf
     * @see java.lang.Double#valueOf
     * @see java.math.BigDecimal#BigDecimal(String)
     */
    public static Number parseNumber(String text, Class<?> targetClass) {
        Assert.notNull(text, "Text must not be null");
        Assert.notNull(targetClass, "Target class must not be null");
        
        String trimmed = text.trim();
        
        if (targetClass.equals(Byte.class)) {
            return Byte.decode(trimmed);
        } else if (targetClass.equals(Short.class)) {
            return Short.decode(trimmed);
        } else if (targetClass.equals(Integer.class)) {
            return Integer.decode(trimmed);
        } else if (targetClass.equals(Long.class)) {
            return Long.decode(trimmed);
        } else if (targetClass.equals(BigInteger.class)) {
            return decodeBigInteger(trimmed);
        } else if (targetClass.equals(Float.class)) {
            return Float.valueOf(trimmed);
        } else if (targetClass.equals(Double.class)) {
            return Double.valueOf(trimmed);
        } else if (targetClass.equals(BigDecimal.class) || targetClass.equals(Number.class)) {
            return new BigDecimal(trimmed);
        } else {
            throw new IllegalArgumentException("Cannot convert String [" + text
                + "] to target class [" + targetClass.getName() + "]");
        }
    }
    
    /**
     * Parse the given text into a number instance of the given target class,
     * using the given NumberFormat. Trims the input <code>String</code> before
     * attempting to parse the number.
     * 
     * @param text
     *            the text to convert
     * @param targetClass
     *            the target class to parse into
     * @param numberFormat
     *            the NumberFormat to use for parsing (if <code>null</code>,
     *            this method falls back to
     *            <code>parseNumber(String, Class)</code>)
     * @return the parsed number
     * @throws IllegalArgumentException
     *             if the target class is not supported (i.e. not a standard
     *             Number subclass as included in the JDK)
     * @see java.text.NumberFormat#parse
     * @see #convertNumberToTargetClass
     * @see #parseNumber(String, Class)
     */
    public static Number parseNumber(String text, Class<?> targetClass, NumberFormat numberFormat) {
        if (numberFormat != null) {
            Assert.notNull(text, "Text must not be null");
            Assert.notNull(targetClass, "Target class must not be null");
            try {
                Number number = numberFormat.parse(text.trim());
                return convertNumberToTargetClass(number, targetClass);
            } catch (ParseException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            }
        } else {
            return parseNumber(text, targetClass);
        }
    }
    
    public static long parseLong(String value) {
        if (!StringUtils.isNotBlank(value)) {
            return 0;
        }
        NumberFormat df = NumberFormat.getInstance();
        long result;
        try {
            result = df.parse(value).longValue();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            result = 0;
        }
        return result;
    }
    
    /**
     * Decode a {@link java.math.BigInteger} from a {@link String} value.
     * Supports decimal, hex and octal notation.
     * 
     * @see BigInteger#BigInteger(String, int)
     */
    private static BigInteger decodeBigInteger(String value) {
        int radix = 10;
        int index = 0;
        boolean negative = false;
        
        // Handle minus sign, if present.
        if (value.startsWith("-")) {
            negative = true;
            index++;
        }
        
        // Handle radix specifier, if present.
        if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (value.startsWith("#", index)) {
            index++;
            radix = 16;
        } else if (value.startsWith("0", index) && value.length() > 1 + index) {
            index++;
            radix = 8;
        }
        
        BigInteger result = new BigInteger(value.substring(index), radix);
        return (negative ? result.negate()
            : result);
    }
    
    public static int parseInt(String value) {
        if (!StringUtils.isNotBlank(value)) {
            return 0;
        }
        NumberFormat df = NumberFormat.getInstance();
        int result;
        try {
            result = df.parse(value).intValue();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            result = 0;
        }
        return result;
    }
    
    public static float parseFloat(String value) {
        if (!StringUtils.isNotBlank(value)) {
            return 0;
        }
        NumberFormat df = NumberFormat.getInstance();
        float result;
        try {
            result = df.parse(value).floatValue();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            result = 0;
        }
        return result;
    }
    
    public static double parseDouble(String value) {
        if (!StringUtils.isNotBlank(value)) {
            return 0;
        }
        NumberFormat df = NumberFormat.getInstance();
        double result;
        try {
            result = df.parse(value).doubleValue();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            result = 0;
        }
        return result;
    }
    
    public static short parseShort(String value) {
        if (!StringUtils.isNotBlank(value)) {
            return 0;
        }
        NumberFormat df = NumberFormat.getInstance();
        short result;
        try {
            result = df.parse(value).shortValue();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            result = 0;
        }
        return result;
    }
    
    /**
     * 将给定的数值格式为百分比表示
     * 
     * @param d
     * @return
     */
    public static String formatToPercent(double d) {
        if (d == 0)
            return String.valueOf(d);
        d = d * 100;
        return truncate(String.valueOf(d), 2) + "%";
    }
    
    /**
     * 将给定数字截取到小数点后几位
     * 
     * @param d
     * @param accuracy
     *            精度 ，保留小数点后的位数
     * @return
     */
    public static String truncate(String d, int accuracy) {
        if (!StringUtils.isNotBlank(d))
            return "";
        int index = d.indexOf(".");
        if (index > 0) {
            d = d.substring(0, (index + accuracy + 1) > d.length() ? d.length()
                : (index + accuracy + 1));
        }
        return d;
    }
    
    /**
     * 将给定的数值格式为易于理解的表示，如20000转换为2万
     * 
     * @param d
     * @return
     */
    public static String formatToSimple(double d) {
        if (d == 0)
            return String.valueOf(d);
        int tt = (int) d / 10000;
        if (tt > 0) {
            d = d / 10000;
            return truncate(String.valueOf(d), 2) + "万";
        }
        return truncate(String.valueOf(d), 2);
    }
    
    /**
     * <p>
     * Convert a <code>String</code> to an <code>int</code>, returning
     * <code>zero</code> if the conversion fails.
     * </p>
     * 
     * <p>
     * If the string is <code>null</code>, <code>zero</code> is returned.
     * </p>
     * 
     * <pre>
     *   NumberUtils.toInt(null) = 0
     *   NumberUtils.toInt("")   = 0
     *   NumberUtils.toInt("1")  = 1
     * </pre>
     * 
     * @param str
     *            the string to convert, may be null
     * @return the int represented by the string, or <code>zero</code> if
     *         conversion fails
     * @since 2.1
     */
    public static int toInt(String str) {
        return toInt(str, 0);
    }
    
    /**
     * <p>
     * Convert a <code>String</code> to an <code>int</code>, returning a default
     * value if the conversion fails.
     * </p>
     * 
     * <p>
     * If the string is <code>null</code>, the default value is returned.
     * </p>
     * 
     * <pre>
     *   NumberUtils.toInt(null, 1) = 1
     *   NumberUtils.toInt("", 1)   = 1
     *   NumberUtils.toInt("1", 0)  = 1
     * </pre>
     * 
     * @param str
     *            the string to convert, may be null
     * @param defaultValue
     *            the default value
     * @return the int represented by the string, or the default if conversion
     *         fails
     * @since 2.1
     */
    public static int toInt(String str, int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
    
    /**
     * <p>
     * Convert a <code>String</code> to a <code>long</code>, returning
     * <code>zero</code> if the conversion fails.
     * </p>
     * 
     * <p>
     * If the string is <code>null</code>, <code>zero</code> is returned.
     * </p>
     * 
     * <pre>
     *   NumberUtils.toLong(null) = 0L
     *   NumberUtils.toLong("")   = 0L
     *   NumberUtils.toLong("1")  = 1L
     * </pre>
     * 
     * @param str
     *            the string to convert, may be null
     * @return the long represented by the string, or <code>0</code> if
     *         conversion fails
     * @since 2.1
     */
    public static long toLong(String str) {
        return toLong(str, 0L);
    }
    
    /**
     * <p>
     * Convert a <code>String</code> to a <code>long</code>, returning a default
     * value if the conversion fails.
     * </p>
     * 
     * <p>
     * If the string is <code>null</code>, the default value is returned.
     * </p>
     * 
     * <pre>
     *   NumberUtils.toLong(null, 1L) = 1L
     *   NumberUtils.toLong("", 1L)   = 1L
     *   NumberUtils.toLong("1", 0L)  = 1L
     * </pre>
     * 
     * @param str
     *            the string to convert, may be null
     * @param defaultValue
     *            the default value
     * @return the long represented by the string, or the default if conversion
     *         fails
     * @since 2.1
     */
    public static long toLong(String str, long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
    
    /**
     * <p>
     * Convert a <code>String</code> to a <code>float</code>, returning
     * <code>0.0f</code> if the conversion fails.
     * </p>
     * 
     * <p>
     * If the string <code>str</code> is <code>null</code>, <code>0.0f</code> is
     * returned.
     * </p>
     * 
     * <pre>
     *   NumberUtils.toFloat(null)   = 0.0f
     *   NumberUtils.toFloat("")     = 0.0f
     *   NumberUtils.toFloat("1.5")  = 1.5f
     * </pre>
     * 
     * @param str
     *            the string to convert, may be <code>null</code>
     * @return the float represented by the string, or <code>0.0f</code> if
     *         conversion fails
     * @since 2.1
     */
    public static float toFloat(String str) {
        return toFloat(str, 0.0f);
    }
    
    /**
     * <p>
     * Convert a <code>String</code> to a <code>float</code>, returning a
     * default value if the conversion fails.
     * </p>
     * 
     * <p>
     * If the string <code>str</code> is <code>null</code>, the default value is
     * returned.
     * </p>
     * 
     * <pre>
     *   NumberUtils.toFloat(null, 1.1f)   = 1.0f
     *   NumberUtils.toFloat("", 1.1f)     = 1.1f
     *   NumberUtils.toFloat("1.5", 0.0f)  = 1.5f
     * </pre>
     * 
     * @param str
     *            the string to convert, may be <code>null</code>
     * @param defaultValue
     *            the default value
     * @return the float represented by the string, or defaultValue if
     *         conversion fails
     * @since 2.1
     */
    public static float toFloat(String str, float defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
    
    /**
     * <p>
     * Convert a <code>String</code> to a <code>double</code>, returning
     * <code>0.0d</code> if the conversion fails.
     * </p>
     * 
     * <p>
     * If the string <code>str</code> is <code>null</code>, <code>0.0d</code> is
     * returned.
     * </p>
     * 
     * <pre>
     *   NumberUtils.toDouble(null)   = 0.0d
     *   NumberUtils.toDouble("")     = 0.0d
     *   NumberUtils.toDouble("1.5")  = 1.5d
     * </pre>
     * 
     * @param str
     *            the string to convert, may be <code>null</code>
     * @return the double represented by the string, or <code>0.0d</code> if
     *         conversion fails
     * @since 2.1
     */
    public static double toDouble(String str) {
        return toDouble(str, 0.0d);
    }
    
    /**
     * <p>
     * Convert a <code>String</code> to a <code>double</code>, returning a
     * default value if the conversion fails.
     * </p>
     * 
     * <p>
     * If the string <code>str</code> is <code>null</code>, the default value is
     * returned.
     * </p>
     * 
     * <pre>
     *   NumberUtils.toDouble(null, 1.1d)   = 1.1d
     *   NumberUtils.toDouble("", 1.1d)     = 1.1d
     *   NumberUtils.toDouble("1.5", 0.0d)  = 1.5d
     * </pre>
     * 
     * @param str
     *            the string to convert, may be <code>null</code>
     * @param defaultValue
     *            the default value
     * @return the double represented by the string, or defaultValue if
     *         conversion fails
     * @since 2.1
     */
    public static double toDouble(String str, double defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
    
    // -----------------------------------------------------------------------
    /**
     * <p>
     * Convert a <code>String</code> to a <code>byte</code>, returning
     * <code>zero</code> if the conversion fails.
     * </p>
     * 
     * <p>
     * If the string is <code>null</code>, <code>zero</code> is returned.
     * </p>
     * 
     * <pre>
     *   NumberUtils.toByte(null) = 0
     *   NumberUtils.toByte("")   = 0
     *   NumberUtils.toByte("1")  = 1
     * </pre>
     * 
     * @param str
     *            the string to convert, may be null
     * @return the byte represented by the string, or <code>zero</code> if
     *         conversion fails
     * @since 2.5
     */
    public static byte toByte(String str) {
        return toByte(str, (byte) 0);
    }
    
    /**
     * <p>
     * Convert a <code>String</code> to a <code>byte</code>, returning a default
     * value if the conversion fails.
     * </p>
     * 
     * <p>
     * If the string is <code>null</code>, the default value is returned.
     * </p>
     * 
     * <pre>
     *   NumberUtils.toByte(null, 1) = 1
     *   NumberUtils.toByte("", 1)   = 1
     *   NumberUtils.toByte("1", 0)  = 1
     * </pre>
     * 
     * @param str
     *            the string to convert, may be null
     * @param defaultValue
     *            the default value
     * @return the byte represented by the string, or the default if conversion
     *         fails
     * @since 2.5
     */
    public static byte toByte(String str, byte defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Byte.parseByte(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
    
    /**
     * <p>
     * Convert a <code>String</code> to a <code>short</code>, returning
     * <code>zero</code> if the conversion fails.
     * </p>
     * 
     * <p>
     * If the string is <code>null</code>, <code>zero</code> is returned.
     * </p>
     * 
     * <pre>
     *   NumberUtils.toShort(null) = 0
     *   NumberUtils.toShort("")   = 0
     *   NumberUtils.toShort("1")  = 1
     * </pre>
     * 
     * @param str
     *            the string to convert, may be null
     * @return the short represented by the string, or <code>zero</code> if
     *         conversion fails
     * @since 2.5
     */
    public static short toShort(String str) {
        return toShort(str, (short) 0);
    }
    
    /**
     * <p>
     * Convert a <code>String</code> to an <code>short</code>, returning a
     * default value if the conversion fails.
     * </p>
     * 
     * <p>
     * If the string is <code>null</code>, the default value is returned.
     * </p>
     * 
     * <pre>
     *   NumberUtils.toShort(null, 1) = 1
     *   NumberUtils.toShort("", 1)   = 1
     *   NumberUtils.toShort("1", 0)  = 1
     * </pre>
     * 
     * @param str
     *            the string to convert, may be null
     * @param defaultValue
     *            the default value
     * @return the short represented by the string, or the default if conversion
     *         fails
     * @since 2.5
     */
    public static short toShort(String str, short defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Short.parseShort(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
    
    // Min in array
    // --------------------------------------------------------------------
    /**
     * <p>
     * Returns the minimum value in an array.
     * </p>
     * 
     * @param array
     *            an array, must not be null or empty
     * @return the minimum value in the array
     * @throws IllegalArgumentException
     *             if <code>array</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if <code>array</code> is empty
     */
    public static long min(long[] array) {
        // Validates input
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }
        
        // Finds and returns min
        long min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        
        return min;
    }
    
    /**
     * <p>
     * Returns the minimum value in an array.
     * </p>
     * 
     * @param array
     *            an array, must not be null or empty
     * @return the minimum value in the array
     * @throws IllegalArgumentException
     *             if <code>array</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if <code>array</code> is empty
     */
    public static int min(int[] array) {
        // Validates input
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }
        
        // Finds and returns min
        int min = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] < min) {
                min = array[j];
            }
        }
        
        return min;
    }
    
    /**
     * <p>
     * Returns the minimum value in an array.
     * </p>
     * 
     * @param array
     *            an array, must not be null or empty
     * @return the minimum value in the array
     * @throws IllegalArgumentException
     *             if <code>array</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if <code>array</code> is empty
     */
    public static short min(short[] array) {
        // Validates input
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }
        
        // Finds and returns min
        short min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        
        return min;
    }
    
    /**
     * <p>
     * Returns the minimum value in an array.
     * </p>
     * 
     * @param array
     *            an array, must not be null or empty
     * @return the minimum value in the array
     * @throws IllegalArgumentException
     *             if <code>array</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if <code>array</code> is empty
     */
    public static byte min(byte[] array) {
        // Validates input
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }
        
        // Finds and returns min
        byte min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        
        return min;
    }
    
    /**
     * <p>
     * Returns the minimum value in an array.
     * </p>
     * 
     * @param array
     *            an array, must not be null or empty
     * @return the minimum value in the array
     * @throws IllegalArgumentException
     *             if <code>array</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if <code>array</code> is empty
     */
    public static double min(double[] array) {
        // Validates input
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }
        
        // Finds and returns min
        double min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (Double.isNaN(array[i])) {
                return Double.NaN;
            }
            if (array[i] < min) {
                min = array[i];
            }
        }
        
        return min;
    }
    
    /**
     * <p>
     * Returns the minimum value in an array.
     * </p>
     * 
     * @param array
     *            an array, must not be null or empty
     * @return the minimum value in the array
     * @throws IllegalArgumentException
     *             if <code>array</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if <code>array</code> is empty
     */
    public static float min(float[] array) {
        // Validates input
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }
        
        // Finds and returns min
        float min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (Float.isNaN(array[i])) {
                return Float.NaN;
            }
            if (array[i] < min) {
                min = array[i];
            }
        }
        
        return min;
    }
    
    // Max in array
    // --------------------------------------------------------------------
    /**
     * <p>
     * Returns the maximum value in an array.
     * </p>
     * 
     * @param array
     *            an array, must not be null or empty
     * @return the minimum value in the array
     * @throws IllegalArgumentException
     *             if <code>array</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if <code>array</code> is empty
     */
    public static long max(long[] array) {
        // Validates input
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }
        
        // Finds and returns max
        long max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] > max) {
                max = array[j];
            }
        }
        
        return max;
    }
    
    /**
     * <p>
     * Returns the maximum value in an array.
     * </p>
     * 
     * @param array
     *            an array, must not be null or empty
     * @return the minimum value in the array
     * @throws IllegalArgumentException
     *             if <code>array</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if <code>array</code> is empty
     */
    public static int max(int[] array) {
        // Validates input
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }
        
        // Finds and returns max
        int max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] > max) {
                max = array[j];
            }
        }
        
        return max;
    }
    
    /**
     * <p>
     * Returns the maximum value in an array.
     * </p>
     * 
     * @param array
     *            an array, must not be null or empty
     * @return the minimum value in the array
     * @throws IllegalArgumentException
     *             if <code>array</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if <code>array</code> is empty
     */
    public static short max(short[] array) {
        // Validates input
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }
        
        // Finds and returns max
        short max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        
        return max;
    }
    
    /**
     * <p>
     * Returns the maximum value in an array.
     * </p>
     * 
     * @param array
     *            an array, must not be null or empty
     * @return the minimum value in the array
     * @throws IllegalArgumentException
     *             if <code>array</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if <code>array</code> is empty
     */
    public static byte max(byte[] array) {
        // Validates input
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }
        
        // Finds and returns max
        byte max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        
        return max;
    }
    
    /**
     * <p>
     * Returns the maximum value in an array.
     * </p>
     * 
     * @param array
     *            an array, must not be null or empty
     * @return the minimum value in the array
     * @throws IllegalArgumentException
     *             if <code>array</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if <code>array</code> is empty
     */
    public static double max(double[] array) {
        // Validates input
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }
        
        // Finds and returns max
        double max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (Double.isNaN(array[j])) {
                return Double.NaN;
            }
            if (array[j] > max) {
                max = array[j];
            }
        }
        
        return max;
    }
    
    /**
     * <p>
     * Returns the maximum value in an array.
     * </p>
     * 
     * @param array
     *            an array, must not be null or empty
     * @return the minimum value in the array
     * @throws IllegalArgumentException
     *             if <code>array</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if <code>array</code> is empty
     */
    public static float max(float[] array) {
        // Validates input
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }
        
        // Finds and returns max
        float max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (Float.isNaN(array[j])) {
                return Float.NaN;
            }
            if (array[j] > max) {
                max = array[j];
            }
        }
        
        return max;
    }
    
    // 3 param min
    // -----------------------------------------------------------------------
    /**
     * <p>
     * Gets the minimum of three <code>long</code> values.
     * </p>
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the smallest of the values
     */
    public static long min(long a, long b, long c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            a = c;
        }
        return a;
    }
    
    /**
     * <p>
     * Gets the minimum of three <code>int</code> values.
     * </p>
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the smallest of the values
     */
    public static int min(int a, int b, int c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            a = c;
        }
        return a;
    }
    
    /**
     * <p>
     * Gets the minimum of three <code>short</code> values.
     * </p>
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the smallest of the values
     */
    public static short min(short a, short b, short c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            a = c;
        }
        return a;
    }
    
    /**
     * <p>
     * Gets the minimum of three <code>byte</code> values.
     * </p>
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the smallest of the values
     */
    public static byte min(byte a, byte b, byte c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            a = c;
        }
        return a;
    }
    
    /**
     * <p>
     * Gets the minimum of three <code>double</code> values.
     * </p>
     * 
     * <p>
     * If any value is <code>NaN</code>, <code>NaN</code> is returned. Infinity
     * is handled.
     * </p>
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the smallest of the values
     */
    public static double min(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }
    
    /**
     * <p>
     * Gets the minimum of three <code>float</code> values.
     * </p>
     * 
     * <p>
     * If any value is <code>NaN</code>, <code>NaN</code> is returned. Infinity
     * is handled.
     * </p>
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the smallest of the values
     */
    public static float min(float a, float b, float c) {
        return Math.min(Math.min(a, b), c);
    }
    
    // 3 param max
    // -----------------------------------------------------------------------
    /**
     * <p>
     * Gets the maximum of three <code>long</code> values.
     * </p>
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the largest of the values
     */
    public static long max(long a, long b, long c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            a = c;
        }
        return a;
    }
    
    /**
     * <p>
     * Gets the maximum of three <code>int</code> values.
     * </p>
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the largest of the values
     */
    public static int max(int a, int b, int c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            a = c;
        }
        return a;
    }
    
    /**
     * <p>
     * Gets the maximum of three <code>short</code> values.
     * </p>
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the largest of the values
     */
    public static short max(short a, short b, short c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            a = c;
        }
        return a;
    }
    
    /**
     * <p>
     * Gets the maximum of three <code>byte</code> values.
     * </p>
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the largest of the values
     */
    public static byte max(byte a, byte b, byte c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            a = c;
        }
        return a;
    }
    
    /**
     * <p>
     * Gets the maximum of three <code>double</code> values.
     * </p>
     * 
     * <p>
     * If any value is <code>NaN</code>, <code>NaN</code> is returned. Infinity
     * is handled.
     * </p>
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the largest of the values
     */
    public static double max(double a, double b, double c) {
        return Math.max(Math.max(a, b), c);
    }
    
    /**
     * <p>
     * Gets the maximum of three <code>float</code> values.
     * </p>
     * 
     * <p>
     * If any value is <code>NaN</code>, <code>NaN</code> is returned. Infinity
     * is handled.
     * </p>
     * 
     * @param a
     *            value 1
     * @param b
     *            value 2
     * @param c
     *            value 3
     * @return the largest of the values
     */
    public static float max(float a, float b, float c) {
        return Math.max(Math.max(a, b), c);
    }
    
    /**
     * 提供精确的加法运算。
     * 
     * @param v1
     *            被加数
     * @param v2
     *            加数
     * @return 两个参数的和
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static double add(final double v1, final double v2) {
        final BigDecimal b1 = new BigDecimal(Double.toString(v1));
        final BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }
    
    /**
     * 提供精确的减法运算。
     * 
     * @param v1
     *            被减数
     * @param v2
     *            减数
     * @return 两个参数的差
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static double subtract(final double v1, final double v2) {
        final BigDecimal b1 = new BigDecimal(Double.toString(v1));
        final BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }
    
    /**
     * 提供精确的乘法运算。
     * 
     * @param v1
     *            被乘数
     * @param v2
     *            乘数
     * @return 两个参数的积
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static double multiply(final double v1, final double v2) {
        final BigDecimal b1 = new BigDecimal(Double.toString(v1));
        final BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }
    
    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
     * 
     * @param v1
     *            被除数
     * @param v2
     *            除数
     * @return double
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static double divide(final double v1, final double v2) {
        return NumberUtils.divide(v1, v2, NumberUtils.DEF_DIV_SCALE);
    }
    
    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
     * 
     * @param v1
     *            被除数
     * @param v2
     *            除数
     * @param scale
     *            表示表示需要精确到小数点以后几位。
     * @return double 两个参数的商
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static double divide(final double v1, final double v2, final int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        final BigDecimal b1 = new BigDecimal(Double.toString(v1));
        final BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    /**
     * 提供精确的小数位四舍五入处理。
     * 
     * @param v
     *            需要四舍五入的数字
     * @param scale
     *            小数点后保留几位
     * @return 四舍五入后的结果
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static double round(final double v, final int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        final BigDecimal b = new BigDecimal(Double.toString(v));
        final BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    /**
     * 将数字金额(BigDecimal类型)转换为中文金额.
     * 
     * @param bigdMoneyNumber
     *            转换前的数字金额
     * @return String 中文金额
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2004-05-26 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static String lowerToUpperOfMoney(final BigDecimal bigdMoneyNumber) {
        final String[] straChineseUnit = new String[] {"分", "角", "圆", "拾", "佰", "仟", "万", "拾", "佰",
                "仟", "亿", "拾", "佰", "仟" };
        // 中文数字字符数组
        final String[] straChineseNumber = new String[] {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒",
                "捌", "玖" };
        String strChineseCurrency = "";
        // 零数位标记
        boolean bZero = true;
        // 中文金额单位下标
        int chineseUnitIndex = 0;
        
        try {
            if (bigdMoneyNumber.intValue() == 0) {
                return "零圆整";
            }
            // 处理小数部分，四舍五入
            double doubMoneyNumber = Math.round(bigdMoneyNumber.doubleValue() * 100);
            // 是否负数
            final boolean bNegative = doubMoneyNumber < 0;
            // 取绝对值
            doubMoneyNumber = Math.abs(doubMoneyNumber);
            // 循环处理转换操作
            while (doubMoneyNumber > 0) {
                // 整的处理(无小数位)
                if (chineseUnitIndex == 2 && strChineseCurrency.length() == 0) {
                    strChineseCurrency = strChineseCurrency + "整";
                }
                // 非零数位的处理
                if (doubMoneyNumber % 10 > 0) {
                    strChineseCurrency = straChineseNumber[(int) doubMoneyNumber % 10]
                        + straChineseUnit[chineseUnitIndex] + strChineseCurrency;
                    bZero = false;
                } else { // 零数位的处理
                    // 元的处理(个位)
                    if (chineseUnitIndex == 2) {
                        // 段中有数字
                        if (doubMoneyNumber > 0) {
                            strChineseCurrency = straChineseUnit[chineseUnitIndex]
                                + strChineseCurrency;
                            bZero = true;
                        }
                    } else if ((chineseUnitIndex == 6 || chineseUnitIndex == 10)
                        && doubMoneyNumber % 1000 > 0) { // 万、亿数位的处理,段中有数字
                        strChineseCurrency = straChineseUnit[chineseUnitIndex] + strChineseCurrency;
                    }
                    // 前一数位非零的处理
                    if (!bZero) {
                        strChineseCurrency = straChineseNumber[0] + strChineseCurrency;
                    }
                    bZero = true;
                }
                doubMoneyNumber = Math.floor(doubMoneyNumber / 10);
                chineseUnitIndex++;
            }
            // 负数的处理
            if (bNegative) {
                strChineseCurrency = "负" + strChineseCurrency;
            }
        } catch (final Exception e) {
            LOG.error(NumberUtils.class.getName(), e);
            return "";
        }
        return strChineseCurrency;
    }
    
    /**
     * 将小写金额(double类型)转化为人民币大写格式.
     * 
     * @param je
     *            转换前的小写数字金额
     * @return String 中文金额
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2004-05-26 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static String lowerToUpperOfMoney(final double je) {
        String money = ""; // 转换后的字符串
        final String num = "零壹贰叁肆伍陆柒捌玖";
        final String[] unit = {"元", "拾", "佰", "仟", "万", "拾万", "佰万", "仟万", "亿", "拾亿", "佰亿", "仟亿" };
        String s = String.valueOf(je); // 将金额转换为字符串
        final int a = s.indexOf("+"); // 判断s是否包含'+',如1.67E+4
        final int e = s.indexOf("E"); // 判断s是否包含'E',如1.67E+4
        if (je == 0.00) {
            return money;
        }
        // 如果包含'E'(该金额是以科学记数法表示,则转换成普通表示法)
        if (e != -1) {
            int index = 0; // 指数值
            if (a == -1) {
                index = Integer.parseInt(s.substring(e + 1)); // 取得指数值
            } else {
                index = Integer.parseInt(s.substring(a + 1)); // 取得指数值
            }
            final String sub1 = s.substring(0, e); // 取得尾数值
            final int dot = sub1.indexOf("."); // 尾数的小数点位置
            // 如果不含有小数点,则在后面补index个'0'
            if (dot == -1) {
                for (int i = 1; i <= index; i++) {
                    s = sub1 + "0";
                }
            } else { // 如果含有小数点,则向后移动小数点index位
                final String sub11 = sub1.substring(0, dot); // 小数点前面的字串
                String sub12 = sub1.substring(dot + 1); // 小数点后面的字串
                if (index >= sub12.length()) {
                    final int j = index - sub12.length();
                    for (int i = 1; i <= j; i++) {
                        sub12 = sub12 + "0";
                    }
                } else {
                    sub12 = sub12.substring(0, index) + "." + sub12.substring(index);
                }
                s = sub11 + sub12;
            }
        }
        final int sdot = s.indexOf("."); // s中小数点的位置
        String beforeDot = ""; // 小数点前面的字串
        String afterDot = ""; // 小数点后面的字串
        // 如果包含小数点
        if (sdot != -1) {
            beforeDot = s.substring(0, sdot);
            afterDot = s.substring(sdot + 1);
        } else { // 不包含小数点
            beforeDot = s;
        }
        final int bl = beforeDot.length();
        boolean zero = false; // 数字是否为零
        int z = 0; // '0'的个数
        
        // 逐位取数字
        for (int j = 0, i = bl - 1; j <= bl - 1; j++, i--) {
            final int number = Integer.parseInt(String.valueOf(beforeDot.charAt(j)));
            if (number == 0) {
                zero = true;
                z++;
            } else {
                zero = false;
                z = 0;
            }
            if (zero && z == 1) {
                money += "零";
            } else if (!zero) {
                money += num.substring(number, number + 1) + unit[i];
            }
        }
        
        // 删去多余的'万'和'亿'
        for (int i = 1; i <= 2; i++) {
            String ss = "";
            if (i == 1) {
                ss = "万";
            } else {
                ss = "亿";
            }
            final int last = money.lastIndexOf(ss);
            if (last != -1) {
                String moneySub1 = money.substring(0, last);
                final String moneySub2 = money.substring(last, money.length());
                int last2 = moneySub1.indexOf(ss);
                while (last2 != -1) {
                    moneySub1 = moneySub1.substring(0, last2)
                        + moneySub1.substring(last2 + 1, moneySub1.length());
                    last2 = moneySub1.indexOf(ss);
                }
                money = moneySub1 + moneySub2;
            }
        }
        
        // money中是否包含'元'
        final int yuan = money.indexOf("元");
        // 如果不包含'元'
        if (yuan == -1) {
            final int zi = money.lastIndexOf("零");
            // 如果最后一位字符为'零',则删除它
            if (zi == money.length() - 1) {
                money = money.substring(0, money.length() - 1) + "元"; // 在money最后加上'元'
            }
        }
        
        // 如果小数点后面的字串不为空,则处理'角','分'
        if (!"".equals(afterDot)) {
            int al = afterDot.length();
            if (al > 2) { // 如果字串长度大于2,则截断
                afterDot = afterDot.substring(0, 2);
                al = afterDot.length();
            }
            // 如果字符串不为'0'或'00',则处理,否则不进行处理
            if (!"0".equals(afterDot) && !"00".equals(afterDot)) {
                // 逐位取得字符
                for (int i = 0; i < al; i++) {
                    final int number = Integer.parseInt(String.valueOf(afterDot.charAt(i)));
                    if (number != 0 && i == 0) {
                        money += num.substring(number, number + 1) + "角";
                    } else if (number != 0 && i == 1) {
                        money += num.substring(number, number + 1) + "分";
                    } else if (number == 0 && i == 0) {
                        money += "零";
                    }
                }
            }
        }
        // 如果不包含'角','分'则在最后加上'整'字
        if (money.indexOf("角") == -1 && money.indexOf("分") == -1) {
            money += "整";
        }
        return money;
    }
    
    /**
     * 将double数转化为指定位数的字符串.
     * 
     * @param num
     *            待转换的float数
     * @param digits
     *            小数点后的位数
     * @return String 指定小数位数的字符串
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2004-05-26 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static String getNumberFormat(final float num, final int digits) {
        String thenum;
        final NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(digits);
        nf.setMinimumFractionDigits(digits);
        thenum = nf.format(num).toString();
        return thenum;
    }
    
    /**
     * 将double数转化为指定位数的字符串.<br>
     * 例如： NumericUtil.getNumberFormat(123456.12345,3)
     * 的结果为123,456.123，注意小数点最后一位四舍五入
     * 
     * @param num
     *            待转换的double数
     * @param digits
     *            小数点后的位数
     * @return String 指定小数位数的字符串
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期2004-05-26 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static String getNumberFormat(final double num, final int digits) {
        String thenum;
        final NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(digits);
        nf.setMinimumFractionDigits(digits);
        thenum = nf.format(num).toString();
        return thenum;
    }
    
    /**
     * 将BigDecimal数转化为指定位数的字符串.<br>
     * 例如： NumericUtil.getNumberFormat(new BigDecimal(123456.12345),3)
     * 的结果为123,456.123，注意小数点最后一位四舍五入
     * 
     * @param num
     *            待转换的BigDecimal数
     * @param digits
     *            小数点后的位数
     * @return String 指定小数位数的字符串
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static String getNumberFormat(BigDecimal num, final int digits) {
        String thenum = "";
        if (num == null) {
            num = new BigDecimal(0);
        }
        try {
            final NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(digits);
            nf.setMinimumFractionDigits(digits);
            thenum = nf.format(num).toString();
        } catch (final NumberFormatException nfex) {
            throw new NumberFormatException(nfex.toString());
        }
        return thenum;
    }
    
    /**
     * 格式字符串为double输出.
     * 
     * @param lpNumberFormat
     *            待格式化的字符串
     * @return double double数值
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static double getNumberFormatStrToDouble(final String lpNumberFormat) {
        double lpReturnNumber = 0;
        final NumberFormat nf = NumberFormat.getInstance();
        try {
            final Number lpResultNumber = nf.parse(lpNumberFormat);
            lpReturnNumber = lpResultNumber.doubleValue();
        } catch (final ParseException pe) {
            LOG.error(NumberUtils.class.getName(), pe);
        }
        return lpReturnNumber;
    }
    
    /**
     * 格式字符串为float输出.
     * 
     * @param lpNumberFormat
     *            待格式化的字符串
     * @return float float数值
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static float getNumberFormatStrToFloat(final String lpNumberFormat) {
        float lpReturnNumber = 0;
        final NumberFormat nf = NumberFormat.getInstance();
        try {
            final Number lpResultNumber = nf.parse(lpNumberFormat);
            lpReturnNumber = lpResultNumber.floatValue();
        } catch (final ParseException pe) {
            LOG.error(NumberUtils.class.getName(), pe);
        }
        return lpReturnNumber;
    }
    
    /**
     * 将字符串转化为BigDecimal类型.
     * 
     * @param str
     *            待转换的字符串
     * @return BigDecimal 转换后的BigDecimal，如果字符串为null， 那么BigDecimal为new
     *         BigDecimal("0");
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static BigDecimal toBigDecimal(String str) {
        BigDecimal lpReturnValue;
        try {
            if (str == null) {
                str = "0";
            }
            lpReturnValue = new BigDecimal(str);
        } catch (final NumberFormatException nfe) {
            lpReturnValue = new BigDecimal(0);
        }
        return lpReturnValue;
    }
    
    /**
     * 当变量为空时返回零.
     * 
     * @param obj
     *            Object
     * @return int
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static int nullToZero(final Object obj) {
        int result = 0;
        if (obj == null || "".equals(obj.toString())) {
            result = 0;
        } else {
            result = Integer.valueOf(obj.toString()).intValue();
        }
        
        return result;
        
    }
    
    /**
     * 当变量为空时返回零.
     * 
     * @param obj
     *            Object
     * @return BigDecimal
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static BigDecimal nullToBigDecimalZero(final Object obj) {
        if (obj == null || "".equals(obj.toString())) {
            return new BigDecimal("0");
        } else {
            return new BigDecimal(obj.toString());
        }
    }
    
    /**
     * @param obj
     *            Object
     * @return String
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static String nullToStringZero(final Object obj) {
        if (obj == null || "".equals(obj.toString())) {
            return new BigDecimal(0L).toString();
        } else {
            return new BigDecimal(obj.toString()).toString();
        }
    }
    
    /**
     * @param obj
     *            Object
     * @return Long
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static Long nullToLongZero(final Object obj) {
        try {
            if (obj == null || "".equals(obj.toString())) {
                return 0L;
            } else {
                return Long.valueOf(obj.toString());
            }
        } catch (final Exception ex) {
            LOG.error(NumberUtils.class.getName(), ex);
            return 0L;
        }
    }
    
    public static Long nullToLongZero(final Object obj, final Long rpt) {
        
        Long result = nullToLongZero(obj);
        if (result == 0L) {
            result = rpt;
        }
        return result;
    }
    
    public static Integer nullToIntegerZero(final Object obj) {
        try {
            if (obj == null || "".equals(obj.toString())) {
                return 0;
            } else {
                return Integer.valueOf(obj.toString());
            }
        } catch (final Exception ex) {
            LOG.error(NumberUtils.class.getName(), ex);
            return 0;
        }
    }
    
    /**
     * @param obj
     *            Double
     * @return Double
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static Double nullToDoubleZero(final Double obj) {
        if (obj == null) {
            return new Double(0);
        } else {
            final DecimalFormat format = new DecimalFormat("#.000");
            return new Double(format.format(obj));
        }
    }
    
    /**
     * @param obj
     *            Object
     * @return Double
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2007-9-21 chenjun 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static Double nullToDoubleZero(final Object obj) {
        Double result = new Double(0);
        try {
            if (obj == null) {
                result = new Double(0);
            } else {
                result = new Double(obj.toString());
            }
        } catch (final Exception e) {
            LOG.error(NumberUtils.class.getName(), e);
        }
        return result;
        
    }
    
    /**
     * 将对像转换成Float类型，当对像为空时返回0 .
     * 
     * @param obj
     * @return
     * @author Wangjianjun 2012-5-10 Wangjianjun
     */
    public static Float nullToFloatZero(final Object obj) {
        Float result = new Float(0);
        try {
            if (obj != null) {
                result = Float.valueOf(obj.toString());
            }
        } catch (final Exception e) {
            LOG.error(NumberUtils.class.getName(), e);
        }
        return result;
        
    }
    
    /**
     * 方法功能: 转换大Long为int .
     * 
     * @param lon
     *            输入大Long
     * @return int 返回int
     * @author: Liuzhuangfei
     * @修改记录： ==============================================================<br>
     *        日期:2011-7-22 Liuzhuangfei 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static int toInt(Long lon) {
        int lpResult = 0;
        try {
            if (lon == null) {
                lpResult = 0;
            } else {
                lpResult = lon.intValue();
            }
        } catch (final NumberFormatException nfe) {
            LOG.error(NumberUtils.class.getName(), nfe);
        }
        return lpResult;
    }
    
    /**
     * 根据密码长度和是否包含字母参数来生成随机码.
     * 
     * @param length
     *            长度
     * @param hasLetter
     *            是否包含字母
     * @return 随机密码
     * @author huangjb 2011-10-27 huangjb
     */
    public static String getRandomCode(int length, boolean hasLetter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            double random = Math.random();
            if (hasLetter && random < 0.5) {
                Double d = new Double(random * LETTERS.length);
                sb.append(LETTERS[d.intValue()]);
            } else {
                Double d = new Double(random * NUMBERS.length);
                sb.append(NUMBERS[d.intValue()]);
            }
        }
        return sb.toString();
    }
    
    /**
     * 提供精确的乘法运算。
     * 
     * @param v1
     *            被乘数
     * @param v2
     *            乘数
     * @return 两个参数的积
     * @author: chenjun
     * @修改记录： ==============================================================<br>
     *        日期:2014-7-8 laiym 创建方法，并实现其功能
     *        ==============================================================<br>
     */
    public static BigDecimal multiplyRetBig(final double v1, final double v2) {
        final BigDecimal b1 = new BigDecimal(Double.toString(v1));
        final BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }
    
}
