package com.ctg.itrdc.event.utils;

import java.util.List;

public interface Parse {
    public String getCountSql(String sql);
    
    public String getPageSql(String sql);
    
    public List<Object> getPageParam(int page,int pageSize);
}
