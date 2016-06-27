package com.ctg.itrdc.event.utils;

public class EntityColumn {
    
    private String   columnName;
    
    private String   filedName;
    
    private Class<?> type;
    
    public EntityColumn() {
        
    }
    
    public EntityColumn(String columnName, String filedName) {
        super();
        this.columnName = columnName;
        this.filedName = filedName;
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    
    public String getFiledName() {
        return filedName;
    }
    
    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public void setType(Class<?> type) {
        this.type = type;
    }
    
}
