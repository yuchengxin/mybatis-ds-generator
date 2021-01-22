package com.catyee.generator.plugin;

import com.catyee.generator.PlusXmlConfigParser;
import com.catyee.generator.config.JoinDetail;
import com.catyee.generator.config.JoinTarget;
import com.catyee.generator.plugin.generator.GeneratorUtils;
import com.catyee.generator.plugin.generator.JoinMethodJavaMapperGenerator;
import com.catyee.generator.plugin.generator.JoinResultXmlMapperGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.List;

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
        JoinDetail joinDetail = PlusXmlConfigParser.getJoinConfig().getJoinDetail(tableName);
        if (joinDetail != null) {
            addJoinField(topLevelClass, joinDetail);
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
        JoinDetail joinDetail = PlusXmlConfigParser.getJoinConfig().getJoinDetail(tableName);
        if (joinDetail != null) {
            addJoinField(topLevelClass, joinDetail);
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
        JoinDetail joinDetail = PlusXmlConfigParser.getJoinConfig().getJoinDetail(tableName);
        if (joinDetail != null) {
            addJoinField(topLevelClass, joinDetail);
        }
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        JoinDetail joinDetail = PlusXmlConfigParser.getJoinConfig().getJoinDetail(tableName);
        if (joinDetail != null) {
            // 生成join方法
            AbstractJavaMapperMethodGenerator methodGenerator = new JoinMethodJavaMapperGenerator(context, introspectedTable, joinDetail);
            methodGenerator.setContext(context);
            methodGenerator.setIntrospectedTable(introspectedTable);
            methodGenerator.addInterfaceElements(interfaze);

            // 生成join结果的resultMap
            JoinResultXmlMapperGenerator xmlMapperGenerator = new JoinResultXmlMapperGenerator(context, introspectedTable, joinDetail);
            xmlMapperGenerator.generate();
        }
        return true;
    }

    private void addJoinField(TopLevelClass topLevelClass, JoinDetail joinDetail) {
        for (JoinTarget target : joinDetail.getJoinTargets()) {
            IntrospectedTable targetTable = GeneratorUtils.getIntrospectedTable(context, target.getTargetTable());
            FullyQualifiedJavaType recordType = new FullyQualifiedJavaType(targetTable.getBaseRecordType());
            FullyQualifiedJavaType listReturnType = FullyQualifiedJavaType.getNewListInstance();
            listReturnType.addTypeArgument(recordType);
            FullyQualifiedJavaType filedType = target.getType() == JoinTarget.JoinType.MORE ? listReturnType : recordType;
            Field field = new Field(target.getFiledName(), filedType);
            field.setVisibility(JavaVisibility.PRIVATE);
            topLevelClass.addImportedType(recordType);
            if (target.getType() == JoinTarget.JoinType.MORE) {
                topLevelClass.addImportedType(FullyQualifiedJavaType.getNewListInstance());
            }
            topLevelClass.addField(field);
        }
    }
}
