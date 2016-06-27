package com.ctg.itrdc.event.utils;

public abstract class SimplerParse implements Parse {
    
    private static Parse parse = null;
    
    public static Parse getParse(Dialect dialect) {
        if (parse == null) {
            if (dialect == Dialect.mysql) {
                if (parse == null) {
                    parse = new MySqlParse();
                }
            } else if (dialect == Dialect.oracle) {
                if (parse == null) {
                    parse = new OracleParse();
                }
            }
        }
        return parse;
    }
    
    @Override
    public String getCountSql(String sql) {
        StringBuffer countSql = new StringBuffer();
        countSql.append("select count(1) from (");
        countSql.append(SqlDealUtil.repOrderBy(sql));
        countSql.append(") temp_count");
        return countSql.toString();
    }
    
}
