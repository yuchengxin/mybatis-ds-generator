package com.catyee.generator.plugin.generator;

import com.catyee.generator.config.JoinEntry;
import com.catyee.generator.config.JoinTarget;
import org.apache.commons.lang3.tuple.Pair;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.runtime.dynamic.sql.elements.FieldAndImports;
import org.mybatis.generator.runtime.dynamic.sql.elements.FragmentGenerator;
import org.mybatis.generator.runtime.dynamic.sql.elements.MethodAndImports;
import org.mybatis.generator.runtime.dynamic.sql.elements.MethodParts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JoinMethodJavaMapperGenerator extends AbstractJavaMapperMethodGenerator {

    private static final String MYBATIS3_CUSTOM_UTILS = "com.catyee.generator.utils.MyBatis3CustomUtils";

    private final JoinEntry joinEntry;
    private final String tableFieldName;
    private final FragmentGenerator fragmentGenerator;
    private final FullyQualifiedJavaType recordType;
    private final String resultMapId;

    public JoinMethodJavaMapperGenerator(Context context, IntrospectedTable table, JoinEntry joinEntry) {
        this.context = context;
        this.introspectedTable = table;
        this.progressCallback = new NullProgressCallback();
        this.warnings = new ArrayList<>();
        this.joinEntry = joinEntry;
        this.resultMapId = JoinEntry.getJoinResultMapId(introspectedTable.getFullyQualifiedTable().getDomainObjectName());
        this.tableFieldName = JavaBeansUtil.getValidPropertyName(introspectedTable.getFullyQualifiedTable().getDomainObjectName());
        this.recordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        this.fragmentGenerator = new FragmentGenerator.Builder()
                .withIntrospectedTable(introspectedTable)
                .withResultMapId(resultMapId)
                .withTableFieldName(tableFieldName)
                .build();
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        MethodAndImports joinOneMI = generateJoinOneMethodAndImports();
        if (joinOneMI != null) {
            interfaze.addMethod(joinOneMI.getMethod());
            interfaze.addImportedTypes(joinOneMI.getImports());
            interfaze.addStaticImports(joinOneMI.getStaticImports());
        }
        MethodAndImports joinManyMI = generateJoinManyMethodAndImports();
        if (joinManyMI != null) {
            interfaze.addMethod(joinManyMI.getMethod());
            interfaze.addImportedTypes(joinManyMI.getImports());
            interfaze.addStaticImports(joinManyMI.getStaticImports());
        }
        FieldAndImports fieldAndImports = generateLeftJoinFieldAndImports();
        if (fieldAndImports != null) {
            interfaze.addField(fieldAndImports.getField());
            interfaze.addImportedTypes(fieldAndImports.getImports());
            interfaze.addStaticImports(fieldAndImports.getStaticImports());
        }
        MethodAndImports defaultJoinSelectMI = generateDefaultLeftJoinSelectMethodAndImports();
        if (defaultJoinSelectMI != null) {
            interfaze.addMethod(defaultJoinSelectMI.getMethod());
            interfaze.addImportedTypes(defaultJoinSelectMI.getImports());
            interfaze.addStaticImports(defaultJoinSelectMI.getStaticImports());
        }
        MethodAndImports defaultJoinSelectOneMI = generateDefaultLeftJoinSelectOneMethodAndImports();
        if (defaultJoinSelectOneMI != null) {
            interfaze.addMethod(defaultJoinSelectOneMI.getMethod());
            interfaze.addImportedTypes(defaultJoinSelectOneMI.getImports());
            interfaze.addStaticImports(defaultJoinSelectOneMI.getStaticImports());
        }
    }

    /**
     * 添加leftJoinSelectOne方法
     *
     * @return
     */
    private MethodAndImports generateJoinOneMethodAndImports() {
        Set<FullyQualifiedJavaType> imports = new HashSet<>();
        boolean reuseResultMap = introspectedTable.getRules().generateSelectByExampleWithBLOBs()
                || introspectedTable.getRules().generateSelectByExampleWithoutBLOBs();

        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(
                "org.mybatis.dynamic.sql.select.render.SelectStatementProvider");
        imports.add(parameterType);

        FullyQualifiedJavaType adapter = new FullyQualifiedJavaType("org.mybatis.dynamic.sql.util.SqlProviderAdapter");
        imports.add(adapter);

        FullyQualifiedJavaType annotation = new FullyQualifiedJavaType("org.apache.ibatis.annotations.SelectProvider");
        imports.add(annotation);

        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("java.util.Optional");
        returnType.addTypeArgument(recordType);

        imports.add(returnType);
        imports.add(recordType);

        Method method = new Method("leftJoinSelectOne");
        method.setAbstract(true);

        method.setReturnType(returnType);
        method.addParameter(new Parameter(parameterType, "selectStatement"));
        context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, imports);
        method.addAnnotation("@SelectProvider(type=SqlProviderAdapter.class, method=\"select\")");
        MethodAndImports.Builder builder = MethodAndImports.withMethod(method).withImports(imports);

        if (introspectedTable.isConstructorBased()) {
            MethodParts methodParts = fragmentGenerator.getAnnotatedConstructorArgs();
            acceptParts(builder, method, methodParts);
        } else {
            if (reuseResultMap) {
                FullyQualifiedJavaType rmAnnotation = new FullyQualifiedJavaType("org.apache.ibatis.annotations.ResultMap");
                builder.withImport(rmAnnotation);
                method.addAnnotation("@ResultMap(\"" + resultMapId + "\")");
            } else {
                MethodParts methodParts = fragmentGenerator.getAnnotatedResults();
                acceptParts(builder, method, methodParts);
            }
        }
        return builder.build();
    }

    /**
     * 添加leftJoinSelectMany方法
     *
     * @return
     */
    private MethodAndImports generateJoinManyMethodAndImports() {
        if (!introspectedTable.getRules().generateSelectByExampleWithBLOBs()
                && !introspectedTable.getRules().generateSelectByExampleWithoutBLOBs()) {
            return null;
        }

        Set<FullyQualifiedJavaType> imports = new HashSet<>();

        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(
                "org.mybatis.dynamic.sql.select.render.SelectStatementProvider");
        FullyQualifiedJavaType adapter = new FullyQualifiedJavaType("org.mybatis.dynamic.sql.util.SqlProviderAdapter");
        FullyQualifiedJavaType annotation = new FullyQualifiedJavaType("org.apache.ibatis.annotations.SelectProvider");

        imports.add(parameterType);
        imports.add(adapter);
        imports.add(annotation);
        imports.add(FullyQualifiedJavaType.getNewListInstance());
        imports.add(recordType);

        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        returnType.addTypeArgument(recordType);

        Method method = new Method("leftJoinSelectMany");
        method.setAbstract(true);
        method.setReturnType(returnType);
        method.addParameter(new Parameter(parameterType, "selectStatement"));
        context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, imports);
        method.addAnnotation("@SelectProvider(type=SqlProviderAdapter.class, method=\"select\")");

        MethodAndImports.Builder builder = MethodAndImports.withMethod(method).withImports(imports);
        if (introspectedTable.isConstructorBased()) {
            MethodParts methodParts = fragmentGenerator.getAnnotatedConstructorArgs();
            acceptParts(builder, method, methodParts);
        } else {
            FullyQualifiedJavaType rmAnnotation = new FullyQualifiedJavaType("org.apache.ibatis.annotations.ResultMap");
            builder.withImport(rmAnnotation);
            method.addAnnotation("@ResultMap(\"" + resultMapId + "\")");
        }
        return builder.build();
    }

    /**
     * 添加default leftJoinSelect方法
     *
     * @return
     */
    private MethodAndImports generateDefaultLeftJoinSelectMethodAndImports() {
        Set<FullyQualifiedJavaType> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();

        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(
                "org.mybatis.dynamic.sql.select.SelectDSLCompleter");
        imports.add(parameterType);
        imports.add(new FullyQualifiedJavaType(MYBATIS3_CUSTOM_UTILS));

        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        returnType.addTypeArgument(recordType);

        imports.add(returnType);
        imports.add(FullyQualifiedJavaType.getNewListInstance());
        imports.add(FullyQualifiedJavaType.getNewArrayListInstance());
        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.SqlTable"));
        imports.add(new FullyQualifiedJavaType("com.catyee.generator.entity.JoinDetail"));

        Method method = new Method("leftJoinSelect");
        method.setDefault(true);
        method.addParameter(new Parameter(parameterType, "completer"));
        method.setReturnType(returnType);
        method.addBodyLine("List<JoinDetail> joinDetails = new ArrayList<>();");
        for (Pair<String, JoinTarget> detail : joinEntry.getDetails()) {
            String leftTableJoinColumnName = detail.getLeft();
            JoinTarget joinTarget = detail.getRight();
            IntrospectedColumn leftTableJoinColumn = GeneratorUtils.getIntrospectedColumn(
                    introspectedTable, leftTableJoinColumnName
            );

            IntrospectedTable rightJoinTable = GeneratorUtils.getIntrospectedTable(context, joinTarget.getRightTable());
            staticImports.add(GeneratorUtils.getDynamicSqlSupportSubTableType(rightJoinTable));

            String targetTableFieldName = JavaBeansUtil.getValidPropertyName(
                    rightJoinTable.getFullyQualifiedTable().getDomainObjectName()
            );
            IntrospectedColumn targetJoinColumn = GeneratorUtils.getIntrospectedColumn(rightJoinTable, joinTarget.getJoinColumn());
            String targetJoinColumnName = targetTableFieldName + "." + targetJoinColumn.getJavaProperty();
            method.addBodyLine("joinDetails.add(JoinDetail.of(" + leftTableJoinColumn.getJavaProperty() + ", "
                    + targetTableFieldName + ", " + targetJoinColumnName + "));");
        }
        method.addBodyLine("return MyBatis3CustomUtils.leftJoinSelectList(this::leftJoinSelectMany, leftJoinSelectList, " +
                tableFieldName + ", joinDetails , completer);");

        return MethodAndImports.withMethod(method)
                .withImports(imports)
                .withStaticImports(staticImports)
                .build();
    }

    /**
     * 添加default leftJoinSelectOne方法
     *
     * @return
     */
    private MethodAndImports generateDefaultLeftJoinSelectOneMethodAndImports() {
        Set<FullyQualifiedJavaType> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();

        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.SelectDSLCompleter");
        imports.add(parameterType);
        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils"));

        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("java.util.Optional");
        returnType.addTypeArgument(recordType);

        imports.add(returnType);
        imports.add(FullyQualifiedJavaType.getNewListInstance());
        imports.add(FullyQualifiedJavaType.getNewArrayListInstance());
        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.SqlTable"));
        imports.add(new FullyQualifiedJavaType("com.catyee.generator.entity.JoinDetail"));

        Method method = new Method("leftJoinSelectOne");
        method.setDefault(true);
        method.addParameter(new Parameter(parameterType, "completer"));
        method.setReturnType(returnType);
        method.addBodyLine("List<JoinDetail> joinDetails = new ArrayList<>();");
        for (Pair<String, JoinTarget> detail : joinEntry.getDetails()) {
            String leftTableJoinColumnName = detail.getLeft();
            JoinTarget joinTarget = detail.getRight();
            IntrospectedColumn leftTableJoinColumn = GeneratorUtils.getIntrospectedColumn(
                    introspectedTable, leftTableJoinColumnName
            );

            IntrospectedTable rightJoinTable = GeneratorUtils.getIntrospectedTable(context, joinTarget.getRightTable());
            staticImports.add(GeneratorUtils.getDynamicSqlSupportSubTableType(rightJoinTable));
            String targetTableFieldName = JavaBeansUtil.getValidPropertyName(
                    rightJoinTable.getFullyQualifiedTable().getDomainObjectName()
            );
            IntrospectedColumn targetJoinColumn = GeneratorUtils.getIntrospectedColumn(rightJoinTable, joinTarget.getJoinColumn());
            String targetJoinColumnName = targetTableFieldName + "." + targetJoinColumn.getJavaProperty();
            method.addBodyLine("joinDetails.add(JoinDetail.of(" + leftTableJoinColumn.getJavaProperty() + ", "
                    + targetTableFieldName + ", " + targetJoinColumnName + "));");
        }
        method.addBodyLine("return MyBatis3CustomUtils.leftJoinSelectOne(this::leftJoinSelectOne, leftJoinSelectList, " +
                tableFieldName + ", joinDetails , completer);");

        return MethodAndImports.withMethod(method)
                .withImports(imports)
                .withStaticImports(staticImports)
                .build();
    }

    /**
     * 添加left join的域
     *
     * @return
     */
    private FieldAndImports generateLeftJoinFieldAndImports() {
        Set<FullyQualifiedJavaType> imports = new HashSet<>();

        FullyQualifiedJavaType fieldType = new FullyQualifiedJavaType("org.mybatis.dynamic.sql.BasicColumn[]");
        imports.add(fieldType);
        List<IntrospectedTable> joinTargetTables = joinEntry.getDetails().stream().map(
                target -> GeneratorUtils.getIntrospectedTable(context, target.getRight().getRightTable())
        ).collect(Collectors.toList());

        Set<String> staticImports = joinTargetTables.stream().map(
                GeneratorUtils::getDynamicSqlSupportSubTableType
        ).collect(Collectors.toSet());

        Field field = new Field("leftJoinSelectList", fieldType);
        field.setInitializationString("BasicColumn.columnList("
                + fragmentGenerator.getSelectList() + ","
                + joinTargetTables.stream().map(this::getJoinTargetSelectList).collect(Collectors.joining(", "))
                + ")");
        context.getCommentGenerator().addFieldAnnotation(field, introspectedTable, imports);

        return FieldAndImports.withField(field)
                .withImports(imports)
                .withStaticImports(staticImports)
                .build();
    }

    private String getJoinTargetSelectList(IntrospectedTable joinTargetTable) {
        String originJoinTargetTableName = joinTargetTable.getFullyQualifiedTable().getDomainObjectName();
        String targetTableFieldName = JavaBeansUtil.getValidPropertyName(originJoinTargetTableName);
        return joinTargetTable.getAllColumns().stream().map(column -> {
            String actualName = column.getActualColumnName();
            return "\n\t\t(" + targetTableFieldName + "." + column.getJavaProperty() + ").as(\""
                            + GeneratorUtils.generateAliasedColumn(joinTargetTable.getFullyQualifiedTable()
                            .getIntrospectedTableName(), actualName) + "\")";
        }).collect(Collectors.joining(", "));
    }

    private void acceptParts(MethodAndImports.Builder builder, Method method, MethodParts methodParts) {
        for (Parameter parameter : methodParts.getParameters()) {
            method.addParameter(parameter);
        }

        for (String annotation : methodParts.getAnnotations()) {
            method.addAnnotation(annotation);
        }

        method.addBodyLines(methodParts.getBodyLines());
        builder.withImports(methodParts.getImports());
    }
}
