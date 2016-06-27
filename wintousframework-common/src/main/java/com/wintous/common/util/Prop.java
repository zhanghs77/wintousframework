package com.ctg.itrdc.event.utils;

import java.io.Serializable;

public class Prop implements Serializable {
    
    /**
     * .
     */
    private static final long serialVersionUID = 2046814674609843424L;
    
    private String            key              = "";
    
    private String            value            = "";
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
}
