package com.catyee.generator.plugin;

import com.catyee.generator.PlusXmlConfigParser;
import com.catyee.generator.config.JoinEntry;
import com.catyee.generator.config.JoinTarget;
import com.catyee.generator.plugin.generator.GeneratorUtils;
import com.catyee.generator.plugin.generator.JoinMethodJavaMapperGenerator;
import com.catyee.generator.plugin.generator.JoinResultXmlMapperGenerator;
import org.apache.commons.lang3.tuple.Pair;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.List;

import static org.mybatis.generator.internal.util.JavaBeansUtil.*;

public class JoinMethodPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


    /**
     * Intercepts base record class generation
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        JoinEntry joinEntry = PlusXmlConfigParser.getJoinConfig().getJoinEntry(tableName);
        if (joinEntry != null) {
            addJoinField(topLevelClass, joinEntry);
        }
        return true;
    }

    /**
     * Intercepts primary key class generation
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        JoinEntry joinEntry = PlusXmlConfigParser.getJoinConfig().getJoinEntry(tableName);
        if (joinEntry != null) {
            addJoinField(topLevelClass, joinEntry);
        }
        return true;
    }

    /**
     * Intercepts "record with blob" class generation
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        JoinEntry joinEntry = PlusXmlConfigParser.getJoinConfig().getJoinEntry(tableName);
        if (joinEntry != null) {
            addJoinField(topLevelClass, joinEntry);
        }
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        JoinEntry joinEntry = PlusXmlConfigParser.getJoinConfig().getJoinEntry(tableName);
        if (joinEntry != null) {
            // 验证join配置
            joinEntry.validate();

            // 生成join方法
            AbstractJavaMapperMethodGenerator methodGenerator = new JoinMethodJavaMapperGenerator(context, introspectedTable, joinEntry);
            methodGenerator.setContext(context);
            methodGenerator.setIntrospectedTable(introspectedTable);
            methodGenerator.addInterfaceElements(interfaze);

            // 生成join结果的resultMap
            JoinResultXmlMapperGenerator xmlMapperGenerator = new JoinResultXmlMapperGenerator(context, introspectedTable, joinEntry);
            xmlMapperGenerator.generate();
        }
        return true;
    }

    private void addJoinField(TopLevelClass topLevelClass, JoinEntry joinEntry) {
        for (Pair<String, JoinTarget> detail : joinEntry.getDetails()) {
            JoinTarget target = detail.getRight();
            IntrospectedTable targetTable = GeneratorUtils.getIntrospectedTable(context, target.getRightTable());
            FullyQualifiedJavaType recordType = new FullyQualifiedJavaType(targetTable.getBaseRecordType());
            FullyQualifiedJavaType listReturnType = FullyQualifiedJavaType.getNewListInstance();
            listReturnType.addTypeArgument(recordType);
            FullyQualifiedJavaType filedType = target.getType() == JoinTarget.JoinType.MORE ? listReturnType : recordType;

            IntrospectedColumn introspectedColumn = new IntrospectedColumn();
            introspectedColumn.setJavaProperty(target.getFiledName());
            introspectedColumn.setContext(context);
            introspectedColumn.setIntrospectedTable(targetTable);
            introspectedColumn.setFullyQualifiedJavaType(filedType);
            introspectedColumn.setActualColumnName(target.getJoinColumn());

            Field field = getJavaBeansFieldWithGeneratedAnnotation(introspectedColumn, context, targetTable,
                    topLevelClass);
            if (context.getPlugins().modelFieldGenerated(field, topLevelClass, introspectedColumn, targetTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addImportedType(recordType);
                if (target.getType() == JoinTarget.JoinType.MORE) {
                    topLevelClass.addImportedType(FullyQualifiedJavaType.getNewListInstance());
                }
                topLevelClass.addField(field);
            }

            Method method = getJavaBeansGetterWithGeneratedAnnotation(introspectedColumn, context, targetTable,
                    topLevelClass);
            if (context.getPlugins().modelGetterMethodGenerated(method, topLevelClass,
                    introspectedColumn, targetTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addMethod(method);
            }

            if (!targetTable.isImmutable()) {
                method = getJavaBeansSetterWithGeneratedAnnotation(introspectedColumn, context, targetTable,
                        topLevelClass);
                if (context.getPlugins().modelSetterMethodGenerated(method, topLevelClass,
                        introspectedColumn, targetTable,
                        Plugin.ModelClassType.BASE_RECORD)) {
                    topLevelClass.addMethod(method);
                }
            }
        }
    }
}
