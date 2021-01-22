package com.catyee.generator.resolver;

import com.catyee.generator.PlusXmlConfigParser;
import com.catyee.generator.config.CustomTypeDetail;
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
        CustomTypeDetail customTypeDetail = PlusXmlConfigParser.getCustomTypeConfig().getCustomTypeDetail(columnName);
        if (customTypeDetail != null) {
            String className = customTypeDetail.getJavaType();
            if (className != null && !className.isEmpty()) {
                answer = new FullyQualifiedJavaType(className);
                if (customTypeDetail.getGenericTypes() != null) {
                    for (String elementType : customTypeDetail.getGenericTypes()) {
                        FullyQualifiedJavaType elementFullType = new FullyQualifiedJavaType(elementType);
                        answer.addTypeArgument(elementFullType);
                    }
                }
            }

            if (stringHasValue(customTypeDetail.getJavaProperty())) {
                introspectedColumn.setJavaProperty(customTypeDetail.getJavaProperty());
            }

            if (stringHasValue(customTypeDetail.getJdbcType())) {
                introspectedColumn.setJdbcTypeName(customTypeDetail.getJdbcType());
            }

            if (stringHasValue(customTypeDetail.getTypeHandler())) {
                introspectedColumn.setTypeHandler(customTypeDetail.getTypeHandler());
            }

            if (customTypeDetail.isDelimitedColumnName()) {
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
