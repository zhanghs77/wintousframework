package com.ctg.itrdc.event.utils;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlDealUtil {
    
    /**
     * 辅助属性数据配置.
     */
    public static final String                         ASSI_ATTR_SPEC_CONFIG_CLASS = "AssiAttrSpecConfig";
    
    public static final String                         JAVA_CODE_CONVERT_CONFIG    = "orderByConvert";
    
    /**
     * 全部转换
     */
    public static final String                         ALL_CONVERT                 = "all";
    
    /**
     * 注解转换
     */
    public static final String                         HINT_CONVERT                = "hint";
    
    /**
     * 不转换
     */
    public static final String                         NONE_CONVERT                = "none";
    
    private static final ILogger                       LOG                         = LoggerFactory
                                                                                       .getLogger(SqlDealUtil.class);
    
    private static ConcurrentMap<String, List<String>> tableMap                    = new ConcurrentHashMap<String, List<String>>();
    
    private SqlDealUtil() {
    }
    
    /**
     *
     * 方法功能: 根据配置策略不同，走不同的转换方式.
     *
     * @param sql
     * @return
     * @author: lzq
     */
    public static String repOrderBy(String sql) {
        return repAllOrderBy(sql);
    }
    
    /**
     *
     * 方法功能: 根据指定正则表达式将sql语句中的Order by 进行替换 .
     *
     * @param sql
     * @return
     * @author: lzq 备注：担心sql会报错，所以要求有问题的语句需要修改，增加 \\/*_autocount*\\/ 注解
     *          后续如果需要全部一次性替换，可以使用 (?i)[ ]*order[ ]+by[ ]+[\\w,\\.,_]+[
     *          ]*(asc|desc)*
     */
    public static String repHintOrderBy(String sql) {
        LOG.debug("转换前的语句为：" + sql);
        
        String hint = "/*_autocount*/";
        String exp = "(?i)/\\*_autocount\\*/[ ]*order[ ]+by[ ]+(((([\\w,_]+\\(){1}([ ]*(,)*[ ]*([\\w,\\.,_]+){1}[ ]*)*(\\)){1})|([\\w,\\.,_]+)){1}([ ]+(asc|desc))?([ ]+(nulls[ ]*last))?){1}([ ]*(,)[ ]*(((([\\w,_]+\\(){1}([ ]*(,)*[ ]*([\\w,\\.,_]+){1}[ ]*)*(\\)){1})|([\\w,\\.,_]+)){1}([ ]+(asc|desc))?([ ]+(nulls[ ]*last))?){1})*";
        
        LOG.debug("正则表达式：" + exp);
        if (sql != null && sql.toLowerCase().indexOf(hint) > -1) {
            sql = repSqlByExp(sql, exp);
        }
        LOG.debug("转换后的语句为：" + sql);
        return sql;
    }
    
    /**
     *
     * 方法功能: 根据指定正则表达式将sql语句中的Order by 进行替换 .
     *
     * @param sql
     * @return
     * @author: lzq
     */
    public static String repAllOrderBy(String sql) {
        LOG.debug("转换前的语句为：" + sql);
        
        String hint = "/*_autocount*/";
        String shint = "(?i)/\\*_autocount\\*/";
        String exp = "(?i)[ ]*order[ ]+by[ ]+(((([\\w,_]+\\(){1}([ ]*(,)*[ ]*([\\w,\\.,_]+){1}[ ]*)*(\\)){1})|([\\w,\\.,_]+)){1}([ ]+(asc|desc))?([ ]+(nulls[ ]*last))?){1}([ ]*(,)[ ]*(((([\\w,_]+\\(){1}([ ]*(,)*[ ]*([\\w,\\.,_]+){1}[ ]*)*(\\)){1})|([\\w,\\.,_]+)){1}([ ]+(asc|desc))?([ ]+(nulls[ ]*last))?){1})*";
        
        LOG.debug("正则表达式：" + exp);
        
        // 先去除注释
        if (sql != null && sql.toLowerCase().indexOf(hint) > -1) {
            sql = repSqlByExp(sql, shint);
        }
        
        if (sql != null) {
            sql = repSqlByExp(sql, exp);
        }
        LOG.debug("转换后的语句为：" + sql);
        return sql;
    }
    
    /**
     *
     * 方法功能: 根据正则表达式替换语句.
     *
     * @param sql
     * @param exp
     * @return
     * @author: lzq
     * @修改记录：
     */
    public static String repSqlByExp(String sql, String exp) {
        Pattern p = Pattern.compile(exp); // 正则表达式
        Matcher m = p.matcher(sql); // 操作的字符串
        String s = m.replaceAll(" "); // 替换后的字符串
        return s;
    }
    
    public static boolean matchReg(String sql, String exp) {
        Pattern p = Pattern.compile(exp); // 正则表达式
        Matcher m = p.matcher(sql); // 操作的字符串
        return m.matches();
    }
    
    /**
     * 根据表名生成数据库字段
     * @param tableName 表名
     * @param alias 别名
     * @param isLowerCase 是否大小写
     * @return
     */
    public static String createColumn(String tableName, String alias, boolean isLowerCase) {
        StringBuffer sb = new StringBuffer();
        String sql = ClassInfoUtils.getSelectColumns(tableName, alias);
        if (!StringUtils.isNullOrEmpty(sql)) {
            sb.append(sql);
        } else {
            List<String> columnList = new ArrayList<String>();
            if (!tableMap.containsKey(tableName)) {
                Connection connection = null;
                ResultSet set = null;
                try {
                    JdbcTemplate jdbcTemplate = EventContextUtil.getBean("jdbcTemplate");
                    connection = jdbcTemplate.getDataSource().getConnection();
                    DatabaseMetaData dmd = connection.getMetaData();
                    set = dmd.getColumns(null, null, tableName, null);
                    while (set.next()) {
                        columnList.add(set.getString("COLUMN_NAME"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (set != null) {
                            set.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (connection != null) {
                                connection.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                tableMap.putIfAbsent(tableName, columnList);
            } else {
                columnList = tableMap.get(tableName);
            }
            if (columnList != null) {
                sb.append(" ");
                for (int i = 0; i < columnList.size(); i++) {
                    if (i != 0) {
                        sb.append(",");
                    }
                    if (!StringUtils.isNullOrEmpty(alias)) {
                        sb.append(alias + ".");
                    }
                    sb.append(columnList.get(i));
                }
                sb.append(" ");
            }
        }
        if (isLowerCase) {
            return sb.toString().toLowerCase();
        }
        return sb.toString().toUpperCase();
    }
    
    /**
     * 根据表名生成数据库字段,默认大写。
     * @param tableName 表名
     * @param alias 别名
     * @return
     */
    public static String createColumn(String tableName, String alias) {
        return createColumn(tableName, alias, false);
    }
}
