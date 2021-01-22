package com.catyee.generator.plugin.generator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.List;

public class GeneratorUtils {

    public static IntrospectedTable getIntrospectedTable(Context context, String tableName) {
        IntrospectedTable table = getIntrospectedTables(context).stream().filter(introspectedTable ->
                introspectedTable.getFullyQualifiedTable().getIntrospectedTableName().equals(tableName)).findFirst()
                .orElseThrow(() -> new RuntimeException("can not find target table: " + tableName));
        table.initialize();
        return table;
    }

    public static String generateAliasedColumn(String tableName, String columnName) {
        return tableName + "_" + columnName;
    }

    public static IntrospectedColumn getIntrospectedColumn(IntrospectedTable introspectedTable, String columnName) {
        return introspectedTable.getAllColumns().stream().filter(column -> column.getActualColumnName().equals(columnName)).findFirst()
                .orElseThrow(() -> new RuntimeException("can not find target column: " + columnName));
    }

    /**
     * @param introspectedTable
     * @return e.g. TestTableDynamicSqlSupport.testTable
     */
    public static String getDynamicSqlSupportSubTableType(IntrospectedTable introspectedTable) {
        String tableFieldName = JavaBeansUtil.getValidPropertyName(introspectedTable.getFullyQualifiedTable().getDomainObjectName());
        return introspectedTable.getMyBatisDynamicSqlSupportType() + "." + tableFieldName;
    }

    private static List<IntrospectedTable> getIntrospectedTables(Context context) {
        try {
            java.lang.reflect.Field filed = Context.class.getDeclaredField("introspectedTables");
            filed.setAccessible(true);
            return (List<IntrospectedTable>) filed.get(context);
        } catch (Exception e) {
            throw new RuntimeException("failed to get introspected tables by reflecting", e);
        }
    }
}
