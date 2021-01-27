package com.catyee.generator.plugin.generator;

import com.catyee.generator.config.JoinEntry;
import com.catyee.generator.config.JoinTarget;
import org.apache.commons.lang3.tuple.Pair;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.XmlFileMergerJaxp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class JoinResultXmlMapperGenerator {

    private final ShellCallback shellCallback;
    private final Context context;
    private final IntrospectedTable introspectedTable;
    private final JoinEntry joinEntry;

    public JoinResultXmlMapperGenerator(Context context, IntrospectedTable introspectedTable, JoinEntry joinEntry) {
        this.shellCallback = new DefaultShellCallback(true);
        this.context = context;
        this.introspectedTable = introspectedTable;
        this.joinEntry = joinEntry;
    }

    public void generate() {
        GeneratedXmlFile xmlFile = getGeneratedXmlFile();
        writeGeneratedXmlFile(xmlFile);
    }

    private void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("resultMap");
        String joinResultMapId = JoinEntry.getJoinResultMapId(introspectedTable.getFullyQualifiedTable().getDomainObjectName());
        answer.addAttribute(new Attribute("id", joinResultMapId));
        String returnType = introspectedTable.getBaseRecordType();
        answer.addAttribute(new Attribute("type", returnType));

        addResultMapElements(answer);

        parentElement.addElement(answer);
    }

    private void addResultMapElements(XmlElement answer) {
        // add primaryKey
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            XmlElement resultElement = new XmlElement("id");
            resultElement.addAttribute(generateColumnAttribute(introspectedColumn));
            resultElement.addAttribute(new Attribute("property", introspectedColumn.getJavaProperty()));
            resultElement.addAttribute(new Attribute("jdbcType", introspectedColumn.getJdbcTypeName()));

            if (stringHasValue(introspectedColumn.getTypeHandler())) {
                resultElement.addAttribute(new Attribute("typeHandler", introspectedColumn.getTypeHandler()));
            }

            answer.addElement(resultElement);
        }

        // add columns
        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
            XmlElement resultElement = new XmlElement("result");
            resultElement.addAttribute(generateColumnAttribute(introspectedColumn));
            resultElement.addAttribute(new Attribute("property", introspectedColumn.getJavaProperty()));
            resultElement.addAttribute(new Attribute("jdbcType", introspectedColumn.getJdbcTypeName()));

            if (stringHasValue(introspectedColumn.getTypeHandler())) {
                resultElement.addAttribute(new Attribute("typeHandler", introspectedColumn.getTypeHandler()));
            }

            answer.addElement(resultElement);
        }

        List<Pair<String, JoinTarget>> sortResult = sort(joinEntry.getDetails());
        // add join columns
        for (Pair<String, JoinTarget> detail : sortResult) {
            JoinTarget target = detail.getRight();
            XmlElement joinElement = new XmlElement(target.getType() == JoinTarget.JoinType.MORE ? "collection" : "association");
            joinElement.addAttribute(new Attribute("property", target.getFiledName()));
            IntrospectedTable targetTable = GeneratorUtils.getIntrospectedTable(context, target.getRightTable());
            String type = targetTable.getBaseRecordType();
            joinElement.addAttribute(new Attribute(target.getType() == JoinTarget.JoinType.MORE ? "ofType" : "javaType", type));
            addJoinElements(targetTable, joinElement);
            answer.addElement(joinElement);
        }
    }

    private void addJoinElements(IntrospectedTable targetTable, XmlElement joinElement) {
        for (IntrospectedColumn targetColumn : targetTable.getPrimaryKeyColumns()) {
            XmlElement resultElement = new XmlElement("id");
            String targetRenamedColumnName = MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(targetColumn);
            Attribute columnAttribute = new Attribute("column",
                    GeneratorUtils.generateAliasedColumn(targetTable.getFullyQualifiedTable().getIntrospectedTableName(),
                            targetRenamedColumnName));
            resultElement.addAttribute(columnAttribute);
            resultElement.addAttribute(new Attribute("property", targetColumn.getJavaProperty()));
            resultElement.addAttribute(new Attribute("jdbcType", targetColumn.getJdbcTypeName()));

            if (stringHasValue(targetColumn.getTypeHandler())) {
                resultElement.addAttribute(new Attribute("typeHandler", targetColumn.getTypeHandler()));
            }

            joinElement.addElement(resultElement);
        }

        for (IntrospectedColumn targetColumn : targetTable.getNonPrimaryKeyColumns()) {
            XmlElement resultElement = new XmlElement("result");

            String targetRenamedColumnName = MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(targetColumn);
            Attribute columnAttribute = new Attribute("column",
                    GeneratorUtils.generateAliasedColumn(targetTable.getFullyQualifiedTable().getIntrospectedTableName(),
                            targetRenamedColumnName));
            resultElement.addAttribute(columnAttribute);
            resultElement.addAttribute(new Attribute("property", targetColumn.getJavaProperty()));
            resultElement.addAttribute(new Attribute("jdbcType", targetColumn.getJdbcTypeName()));

            if (stringHasValue(targetColumn.getTypeHandler())) {
                resultElement.addAttribute(new Attribute("typeHandler", targetColumn.getTypeHandler()));
            }

            joinElement.addElement(resultElement);
        }
    }

    private List<String> calAllRenamedColumnNames(IntrospectedTable introspectedTable) {
        return introspectedTable.getAllColumns().stream()
                .map(MyBatis3FormattingUtilities::getRenamedColumnNameForResultMap)
                .collect(Collectors.toList());
    }

    private GeneratedXmlFile getGeneratedXmlFile() {
        Document document = getDocument();
        return new GeneratedXmlFile(document, calculateMyBatis3XmlMapperFileName(), calculateSqlMapPackage(),
                joinEntry.getTargetProject(), false, context.getXmlFormatter());
    }

    private Document getDocument() {
        Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID, XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        document.setRootElement(getSqlMapElement());
        if (!context.getPlugins().sqlMapDocumentGenerated(document,
                introspectedTable)) {
            document = null;
        }
        return document;
    }

    private XmlElement getSqlMapElement() {
        XmlElement answer = new XmlElement("mapper");
        String namespace = introspectedTable.getMyBatis3SqlMapNamespace();
        answer.addAttribute(new Attribute("namespace",
                namespace));
        context.getCommentGenerator().addRootComment(answer);

        addElements(answer);

        return answer;
    }

    private String calculateMyBatis3XmlMapperFileName() {
        TableConfiguration tableConfiguration = introspectedTable.getTableConfiguration();
        FullyQualifiedTable fullyQualifiedTable = introspectedTable.getFullyQualifiedTable();
        StringBuilder sb = new StringBuilder();
        if (stringHasValue(tableConfiguration.getMapperName())) {
            String mapperName = tableConfiguration.getMapperName();
            int ind = mapperName.lastIndexOf('.');
            if (ind == -1) {
                sb.append(mapperName);
            } else {
                sb.append(mapperName.substring(ind + 1));
            }
            sb.append(".xml");
        } else {
            sb.append(fullyQualifiedTable.getDomainObjectName());
            sb.append("Mapper.xml");
        }
        return sb.toString();
    }

    private String calculateSqlMapPackage() {
        StringBuilder sb = new StringBuilder();
        TableConfiguration tableConfiguration = introspectedTable.getTableConfiguration();
        FullyQualifiedTable fullyQualifiedTable = introspectedTable.getFullyQualifiedTable();

        sb.append(joinEntry.getTargetPackage());
        sb.append(fullyQualifiedTable.getSubPackageForClientOrSqlMap(false));
        if (stringHasValue(tableConfiguration.getMapperName())) {
            String mapperName = tableConfiguration.getMapperName();
            int ind = mapperName.lastIndexOf('.');
            if (ind != -1) {
                sb.append('.').append(mapperName.substring(0, ind));
            }
        } else if (stringHasValue(fullyQualifiedTable.getDomainObjectSubPackage())) {
            sb.append('.').append(fullyQualifiedTable.getDomainObjectSubPackage());
        }

        return sb.toString();
    }


    private Attribute generateColumnAttribute(IntrospectedColumn introspectedColumn) {
        return new Attribute("column", MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(introspectedColumn));
    }

    private void writeGeneratedXmlFile(GeneratedXmlFile gxf) {
        File targetFile;
        String source;
        try {
            File directory = shellCallback.getDirectory(gxf.getTargetProject(), gxf.getTargetPackage());
            targetFile = new File(directory, gxf.getFileName());
            if (targetFile.exists()) {
                if (gxf.isMergeable()) {
                    source = XmlFileMergerJaxp.getMergedSource(gxf, targetFile);
                } else if (shellCallback.isOverwriteEnabled()) {
                    source = gxf.getFormattedContent();
                } else {
                    source = gxf.getFormattedContent();
                    targetFile = getUniqueFileName(directory, gxf.getFileName());
                }
            } else {
                source = gxf.getFormattedContent();
            }

            writeFile(targetFile, source, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("write file failed.", e);
        }
    }

    /**
     * Gets the unique file name.
     *
     * @param directory the directory
     * @param fileName  the file name
     * @return the unique file name
     */
    private File getUniqueFileName(File directory, String fileName) {
        File answer = null;

        // try up to 1000 times to generate a unique file name
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < 1000; i++) {
            sb.setLength(0);
            sb.append(fileName);
            sb.append('.');
            sb.append(i);

            File testFile = new File(directory, sb.toString());
            if (!testFile.exists()) {
                answer = testFile;
                break;
            }
        }

        if (answer == null) {
            throw new RuntimeException(getString("RuntimeError.3", directory.getAbsolutePath()));
        }

        return answer;
    }

    /**
     * Writes, or overwrites, the contents of the specified file.
     *
     * @param file         the file
     * @param content      the content
     * @param fileEncoding the file encoding
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }

        try (BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write(content);
        }
    }

    private List<Pair<String, JoinTarget>> sort(List<Pair<String, JoinTarget>> joinDetails) {
        List<Pair<String, JoinTarget>> oneTypeJoinDetails = new ArrayList<>();
        List<Pair<String, JoinTarget>> moreTypeJoinDetails = new ArrayList<>();
        for (Pair<String, JoinTarget> detail : joinDetails) {
            if (detail.getRight().getType() == JoinTarget.JoinType.MORE) {
                moreTypeJoinDetails.add(detail);
            }
            if (detail.getRight().getType() == JoinTarget.JoinType.ONE) {
                oneTypeJoinDetails.add(detail);
            }
        }
        oneTypeJoinDetails.addAll(moreTypeJoinDetails);
        return oneTypeJoinDetails;
    }
}
