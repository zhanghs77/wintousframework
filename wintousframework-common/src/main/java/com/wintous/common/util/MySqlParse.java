package com.ctg.itrdc.event.utils;

import java.util.ArrayList;
import java.util.List;

public class MySqlParse extends SimplerParse {
    
    @Override
    public String getPageSql(String sql) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
        sqlBuilder.append(sql);
        sqlBuilder.append(" limit ?,?");
        return sqlBuilder.toString();
    }
    
    @Override
    public List<Object> getPageParam(int page, int pageSize) {
        List<Object> params = new ArrayList<Object>();
        params.add((page - 1) * pageSize);
        params.add(pageSize);
        return params;
    }
    
}
