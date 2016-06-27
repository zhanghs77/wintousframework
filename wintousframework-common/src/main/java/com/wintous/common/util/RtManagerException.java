package com.ctg.itrdc.event.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

@SuppressWarnings("rawtypes")
public class RtManagerException extends RuntimeException {
    
    private static final ILogger LOG              = LoggerFactory
                                                      .getLogger(RtManagerException.class);
    /**
     * .
     */
    private String               clazz            = "";
    
    /**
     * .
     */
    private String               method           = "";
    
    /**
     * .
     */
    private String               code             = "";
    
    /**
     * .
     */
    private Map                  dataMap;
    
    /**
     * The serial version UID of this class. Needed for serialization.
     */
    private static final long    serialVersionUID = 533721739833662664L;
    
    /**
     * The default constructor for <code>ResourceServiceException</code>.
     */
    public RtManagerException() {
    }
    
    /**
     * Constructs a new instance of <code>ResourceServiceException</code>.
     * 
     * @param throwable
     *            the parent Throwable
     */
    public RtManagerException(Throwable throwable) {
        super(findRootCause(throwable));
    }
    
    /**
     * Constructs a new instance of <code>ResourceServiceException</code>.
     * 
     * @param message
     *            the throwable message.
     */
    public RtManagerException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new instance of <code>ResourceServiceException</code>.
     * 例如：new RtManagerException("XX对象为空", this.getClass(), "XXObjNull");
     * 结果：通用提示界面提示信息为：XX对象为空
     * 
     * @param message
     *            String：提示消息.
     * @param clazz
     *            Class： 类.calss，或实例.getClass()
     * @param method
     *            String：异常方法名字
     * @param code
     *            String：本方法内部唯一编码
     */
    public RtManagerException(String message, Class clazz, String method, String code) {
        super(message);
        this.setClazz(clazz);
        this.setMethod(method);
        this.setCode(code);
    }
    
    /**
     * Constructs a new instance of <code>ResourceServiceException</code>.
     * 例如：new RtManagerException("XX对象为空,ID:@id", this.getClass(), "XXObjNull",
     * Map{@id:"123"}); 结果：通用提示界面提示信息为：XX对象为空,ID:123
     * 
     * @param message
     *            String：提示消息.
     * @param clazz
     *            Class： 类.calss，或实例.getClass()
     * @param method
     *            String：异常方法名字
     * @param code
     *            String：本方法内部唯一编码
     * @param dataMap
     *            Map:过程数据保存，会在提示界面展示，
     */
    public RtManagerException(String message, Class clazz, String method, String code, Map dataMap) {
        super(message);
        this.setClazz(clazz);
        this.setMethod(method);
        this.setCode(code);
        this.setDataMap(dataMap);
    }
    
    /**
     * Constructs a new instance of <code>ResourceServiceException</code>.
     * 例如：new RtManagerException("XX对象为空,ID:@id", this.getClass(), "XXObjNull",
     * "@id", "123"); 结果：通用提示界面提示信息为：XX对象为空,ID:123
     * 
     * @param message
     *            String：提示消息.
     * @param clazz
     *            Class： 类.calss，或实例.getClass()
     * @param method
     *            String：异常方法名字
     * @param code
     *            String：本方法内部唯一编码
     * @param params
     *            Object...: 可变数量参数，过程数据保存，必须为偶数，奇数代表键值，偶数代表实际数据，会在提示界面展示
     */
    @SuppressWarnings("unchecked")
    public RtManagerException(String message, Class clazz, String method, String code,
        final Object... params) {
        super(message);
        this.setClazz(clazz);
        this.setMethod(method);
        this.setCode(code);
        // 错误数据保存
        int sum = params.length;
        if (sum > 1) {
            int rsum = sum / 2;
            Map map = new HashMap();
            for (int i = 0, iLength = rsum; i < iLength; i++) {
                map.put(params[i * 2], params[i * 2 + 1]);
            }
            this.setDataMap(map);
        }
    }
    
    /**
     * Constructs a new instance of <code>ResourceServiceException</code>.
     * 
     * @param message
     *            the throwable message.
     * @param throwable
     *            the parent of this Throwable.
     */
    public RtManagerException(String message, Throwable throwable) {
        super(message, findRootCause(throwable));
    }
    
    /**
     * Constructs a new instance of <code>ResourceServiceException</code>.
     * 
     * @param message
     *            the throwable message.
     * @param throwable
     *            the parent of this Throwable.
     * @param String
     *            the throwable code.
     */
    public RtManagerException(String message, Throwable throwable, String code) {
        super(message, findRootCause(throwable));
        this.setCode(code);
    }
    
    /**
     * Finds the root cause of the parent exception by traveling up the
     * exception tree
     */
    private static Throwable findRootCause(Throwable th) {
        if (th != null) {
            // Reflectively get any exception causes.
            try {
                Throwable targetException = null;
                
                // java.lang.reflect.InvocationTargetException
                String exceptionProperty = "targetException";
                if (PropertyUtils.isReadable(th, exceptionProperty)) {
                    targetException = (Throwable) PropertyUtils.getProperty(th, exceptionProperty);
                } else {
                    exceptionProperty = "causedByException";
                    // javax.ejb.EJBException
                    if (PropertyUtils.isReadable(th, exceptionProperty)) {
                        targetException = (Throwable) PropertyUtils.getProperty(th,
                            exceptionProperty);
                    }
                }
                if (targetException != null) {
                    th = targetException;
                }
            } catch (Exception ex) {
                LOG.error(RtManagerException.class.getName(), ex);
            }
            
            if (th.getCause() != null) {
                th = th.getCause();
                th = findRootCause(th);
            }
        }
        return th;
    }
    
    public String getClazz() {
        return clazz;
    }
    
    public void setClazz(Class clazz) {
        this.clazz = clazz.getName();
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getEcode() {
        return clazz + method + code;
    }
    
    public String getEcodeForDisp() {
        return "类：" + clazz + "，方法：" + method + "，编码：" + code;
    }
    
    public Map getDataMap() {
        return dataMap;
    }
    
    public void setDataMap(Map dataMap) {
        this.dataMap = dataMap;
    }
    
    @Override
    public String getMessage() {
        String retMsg = super.getMessage();
        if (retMsg != null && this.dataMap != null && !this.dataMap.isEmpty()) {
            try {
                Iterator it = this.dataMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = entry.getKey() + "";
                    String value = entry.getValue() + "";
                    retMsg = retMsg.replaceAll(key, value);
                }
            } catch (final Exception e) {
                LOG.error(RtManagerException.class.getName(), e);
            }
        }
        return retMsg;
    }
}
