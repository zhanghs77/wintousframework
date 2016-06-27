package com.ctg.itrdc.event.utils;

import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XStreamUtils {
    private static final ILogger LOGGER  = LoggerFactory.getLogger(XStreamUtils.class);
    
    private static XStream       xStream = null;
    
    public static XStream getxStream() {
        return xStream;
    }
    
    static {
        xStream = new XStream(new DomDriver());
    }
    
    /**
     * {@inheritDoc}
     * 
     */
    public static Writer objectToSerial(Object obj) {
        Writer w = new java.io.StringWriter();
        return objectToSerial(w, obj);
    }
    
    /**
     * {@inheritDoc}
     * 
     */
    public static Object serialToObject(Reader r, Class<?> clazz) {
        try {
            Object obj = null;
            try {
                obj = clazz.newInstance();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (obj != null) {
                return xStream.fromXML(r, obj);
            } else {
                return xStream.fromXML(r);
            }
        } catch (Exception e) {
            LOGGER.error("xml转换失败", e);
            throw new RtManagerException(e);
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     */
    public static Writer objectToSerial(Writer w, Object obj) {
        try {
            xStream.toXML(obj, w);
        } catch (Exception e) {
            LOGGER.error("xml转换失败", e);
            throw new RtManagerException(e);
        }
        return w;
    }
    
    /**
     * {@inheritDoc}
     * 
     */
    public static Object serialToObject(String str, Class<?> clazz) {
        Reader r = new java.io.StringReader(str);
        return serialToObject(r, clazz);
    }
}
