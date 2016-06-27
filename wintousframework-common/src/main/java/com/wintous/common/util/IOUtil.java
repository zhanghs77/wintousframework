/**
 * Title: IOUtil.java
 * Copyright: Copyright (C) 2002 - 2014 GuangDong Eshore Techonlogy Co. Ltd
 * Company: 广东亿迅科技有限公司 IT系统事业部
 * @author: chillming
 * @version: CRM2.2
 * @time:  2014年3月25日 下午4:33:22 
 */
package com.ctg.itrdc.event.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * IO工具类
 * 
 * @author chillming
 */
public class IOUtil {
    
    /**
     * 关闭对象（不抛异常）
     * 
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
        }
    }
}
