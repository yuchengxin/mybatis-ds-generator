package com.catyee.generator.resolver;

import com.catyee.generator.PlusXmlConfigParser;
import com.catyee.generator.config.CustomTypeEntry;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;


public class JavaTypeCustomResolver extends JavaTypeResolverDefaultImpl {

    @Override
    public FullyQualifiedJavaType calculateJavaType(IntrospectedColumn introspectedColumn) {
        FullyQualifiedJavaType answer = null;
        JdbcTypeInformation jdbcTypeInformation = typeMap.get(introspectedColumn.getJdbcType());

        if (jdbcTypeInformation != null) {
            answer = jdbcTypeInformation.getFullyQualifiedJavaType();
        }

        // 处理自定义的类型
        String columnName = introspectedColumn.getActualColumnName();
        CustomTypeEntry customTypeEntry = PlusXmlConfigParser.getCustomTypeConfig().getCustomTypeDetail(columnName);
        if (customTypeEntry != null) {
            customTypeEntry.validate();
            String className = customTypeEntry.getJavaType();
            if (className != null && !className.isEmpty()) {
                answer = new FullyQualifiedJavaType(className);
                if (customTypeEntry.getGenericTypes() != null) {
                    for (String elementType : customTypeEntry.getGenericTypes()) {
                        FullyQualifiedJavaType elementFullType = new FullyQualifiedJavaType(elementType);
                        answer.addTypeArgument(elementFullType);
                    }
                }
            }

            if (stringHasValue(customTypeEntry.getJavaProperty())) {
                introspectedColumn.setJavaProperty(customTypeEntry.getJavaProperty());
            }

            if (stringHasValue(customTypeEntry.getJdbcType())) {
                introspectedColumn.setJdbcTypeName(customTypeEntry.getJdbcType());
            }

            if (stringHasValue(customTypeEntry.getTypeHandler())) {
                introspectedColumn.setTypeHandler(customTypeEntry.getTypeHandler());
            }

            if (customTypeEntry.isDelimitedColumnName()) {
                introspectedColumn.setColumnNameDelimited(true);
            }
        }

        answer = overrideDefaultType(introspectedColumn, answer);
        return answer;
    }

    private boolean stringHasValue(String s) {
        return s != null && s.length() > 0;
    }
}
