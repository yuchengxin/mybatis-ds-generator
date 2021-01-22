package com.catyee.generator;

import com.catyee.generator.config.*;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.mybatis.generator.config.xml.ParserEntityResolver;
import org.mybatis.generator.config.xml.ParserErrorHandler;
import org.mybatis.generator.exception.XMLParserException;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class PlusXmlConfigParser {
    private static final String PLUS_CONFIG_PATH = "mybatis.ds.generator.config";
    private static final String MAVEN_BASEDIR_HOLDER = "${basedir}";
    private static final String MAVEN_PROJECT_BASEDIR_HOLDER = "${project.basedir}";
    private static final String MAVEN_POM_BASEDIR_HOLDER = "${pom.basedir}";

    private static final String JOIN_CONFIG_ARG = "joinConfig";
    private static final String CUSTOM_TYPE_CONFIG_ARG = "customTypeConfig";
    private static final String JOIN_TARGET_PROJECT_ARG = "targetProject";
    private static final String JOIN_TARGET_PACKAGE_ARG = "targetPackage";
    private static final String JOIN_DETAIL_ARG = "joinDetail";
    private static final String JOIN_TABLE_ARG = "tableName";
    private static final String JOIN_COLUMN_ARG = "joinColumn";
    private static final String JOIN_TARGET_TABLE_ARG = "targetTable";
    private static final String JOIN_JAVA_PROPERTY_ARG = "property";
    private static final String JOIN_TYPE_ARG = "joinType";

    private static final String CUSTOM_TYPE_ARG = "customType";
    private static final String CUSTOM_COLUMN_ARG = "columnName";
    private static final String CUSTOM_JAVA_TYPE_ARG = "javaType";
    private static final String CUSTOM_TYPE_HANDLER_ARG = "typeHandler";
    private static final String CUSTOM_JDBC_TYPE_ARG = "jdbcType";
    private static final String CUSTOM_JAVA_PROPERTY_ARG = "property";
    private static final String CUSTOM_DELIMIT_COLUMN_ARG = "delimitedColumnName";
    private static final String CUSTOM_GENERIC_TYPE_ARG = "genericType";


    private static final List<String> warnings = new ArrayList<>();
    private static final List<String> parseErrors = new ArrayList<>();
    private static JoinConfig joinConfig;
    private static CustomTypeConfig customTypeConfig;

    public synchronized static JoinConfig getJoinConfig() {
        if (joinConfig == null) {
            parse();
        }
        return joinConfig;
    }

    public synchronized static CustomTypeConfig getCustomTypeConfig() {
        if (customTypeConfig == null) {
            parse();
        }
        return customTypeConfig;
    }

    private static void parse() {
        try (FileInputStream fis = new FileInputStream(new File(getPomFilePath()))) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(fis);
            Properties properties = model.getProperties();
            String rawPlusXMLPath = properties.getProperty(PLUS_CONFIG_PATH);
            String plusXMLPath = parsePlaceHolder(rawPlusXMLPath);
            try (InputStream in = new FileInputStream(new File(plusXMLPath))) {
                InputSource is = new InputSource(in);
                parseConfiguration(is);
            }
        } catch (Exception e) {
            throw new RuntimeException("parse plus xml failed", e);
        }

    }

    private static void parseConfiguration(InputSource inputSource) throws IOException, XMLParserException {
        parseErrors.clear();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);

        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new ParserEntityResolver());

            ParserErrorHandler handler = new ParserErrorHandler(warnings, parseErrors);
            builder.setErrorHandler(handler);

            Document document = null;
            try {
                document = builder.parse(inputSource);
            } catch (SAXParseException e) {
                throw new XMLParserException(parseErrors);
            } catch (SAXException e) {
                if (e.getException() == null) {
                    parseErrors.add(e.getMessage());
                } else {
                    parseErrors.add(e.getException().getMessage());
                }
            }

            if (document == null || !parseErrors.isEmpty()) {
                throw new XMLParserException(parseErrors);
            }

            Element rootNode = document.getDocumentElement();
            if (rootNode.getNodeType() == Node.ELEMENT_NODE) {
                parseMyBatisGeneratorPlusConfiguration(rootNode);
            } else {
                throw new XMLParserException(getString("RuntimeError.5")); //$NON-NLS-1$
            }

            if (!parseErrors.isEmpty()) {
                throw new XMLParserException(parseErrors);
            }
        } catch (ParserConfigurationException e) {
            parseErrors.add(e.getMessage());
            throw new XMLParserException(parseErrors);
        }
    }

    private static void parseMyBatisGeneratorPlusConfiguration(Element rootNode) {
        NodeList nodeList = rootNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if (JOIN_CONFIG_ARG.equals(childNode.getNodeName())) { //$NON-NLS-1$
                parseJoinConfig(childNode);
            } else if (CUSTOM_TYPE_CONFIG_ARG.equals(childNode.getNodeName())) {
                parseCustomTypeConfig(childNode);
            }
        }
    }

    private static void parseJoinConfig(Node node) {
        Map<String, JoinDetail> joinDetails = new HashMap<>();
        Properties attributes = parseAttributes(node);
        String targetProject = attributes.getProperty(JOIN_TARGET_PROJECT_ARG); //$NON-NLS-1$
        String targetPackage = attributes.getProperty(JOIN_TARGET_PACKAGE_ARG); //$NON-NLS-1$
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if (JOIN_DETAIL_ARG.equals(childNode.getNodeName())) { //$NON-NLS-1$
                joinDetails.putAll(parseJoinDetail(childNode, targetProject, targetPackage));
            }
        }
        joinConfig = new JoinConfig(targetPackage, targetProject, joinDetails);
    }

    private static Map<String, JoinDetail> parseJoinDetail(Node node, String targetProject, String targetPackage) {
        Properties attributes = parseAttributes(node);
        String sourceTableName = attributes.getProperty(JOIN_TABLE_ARG); //$NON-NLS-1$
        String joinColumn = attributes.getProperty(JOIN_COLUMN_ARG);
        NodeList nodeList = node.getChildNodes();
        List<JoinTarget> targetTables = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if (JOIN_TARGET_TABLE_ARG.equals(childNode.getNodeName())) { //$NON-NLS-1$
                targetTables.add(parseJoinTarget(childNode));
            }
        }
        JoinDetail detail = new JoinDetail(sourceTableName, targetProject, targetPackage, joinColumn, targetTables);
        return Collections.singletonMap(sourceTableName, detail);
    }

    private static JoinTarget parseJoinTarget(Node node) {
        Properties attributes = parseAttributes(node);
        String targetTableName = attributes.getProperty(JOIN_TABLE_ARG);
        JoinTarget.JoinType joinType = JoinTarget.JoinType.valueOf(attributes.getProperty(JOIN_TYPE_ARG));
        String filedName = attributes.getProperty(JOIN_JAVA_PROPERTY_ARG);
        String joinColumn = attributes.getProperty(JOIN_COLUMN_ARG);
        return new JoinTarget(targetTableName, filedName, joinColumn, joinType);
    }

    private static void parseCustomTypeConfig(Node node) {
        Map<String, CustomTypeDetail> listTypeConfigMap = new HashMap<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if (CUSTOM_TYPE_ARG.equals(childNode.getNodeName())) { //$NON-NLS-1$
                listTypeConfigMap.putAll(parseCustomTypeDetailMap(childNode));
            }
        }
        customTypeConfig = new CustomTypeConfig(listTypeConfigMap);
    }

    private static Map<String, CustomTypeDetail> parseCustomTypeDetailMap(Node node) {
        Properties attributes = parseAttributes(node);
        String columnName = attributes.getProperty(CUSTOM_COLUMN_ARG);
        String javaType = attributes.getProperty(CUSTOM_JAVA_TYPE_ARG);
        String typeHandler = attributes.getProperty(CUSTOM_TYPE_HANDLER_ARG);
        String jdbcType = attributes.getProperty(CUSTOM_JDBC_TYPE_ARG);
        String javaProperty = attributes.getProperty(CUSTOM_JAVA_PROPERTY_ARG);
        String isColumnNameDelimited = attributes.getProperty(CUSTOM_DELIMIT_COLUMN_ARG, "false");
        List<String> genericTypes = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if (CUSTOM_GENERIC_TYPE_ARG.equals(childNode.getNodeName())) { //$NON-NLS-1$
                Properties childAttributes = parseAttributes(childNode);
                String elementType = childAttributes.getProperty(CUSTOM_JAVA_TYPE_ARG);
                genericTypes.add(elementType);
            }
        }
        return Collections.singletonMap(columnName,
                new CustomTypeDetail(columnName, javaType, javaProperty, jdbcType, typeHandler, Boolean.parseBoolean(isColumnNameDelimited), genericTypes)
        );
    }

    private static Properties parseAttributes(Node node) {
        Properties attributes = new Properties();
        NamedNodeMap nnm = node.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            Node attribute = nnm.item(i);
            String value = attribute.getNodeValue();
            attributes.put(attribute.getNodeName(), value);
        }

        return attributes;
    }

    private static String parsePlaceHolder(String s) {
        // 由于maven generator命令只能在配置generator的pom文件所在的目录执行，所以认为工作目录就是${basedir} ${project.basedir} ${pom.basedir}所在的目录
        String rootPath = System.getProperty("user.dir");
        s = s.replace(MAVEN_BASEDIR_HOLDER, rootPath);
        s = s.replace(MAVEN_POM_BASEDIR_HOLDER, rootPath);
        s = s.replace(MAVEN_PROJECT_BASEDIR_HOLDER, rootPath);
        return parsePropertyTokens(s);
    }

    private static String getPomFilePath() {
        // 由于maven generator命令只能在配置generator的pom文件所在的目录执行，所以认为工作目录就是pom文件所在的目录
        String rootPath = System.getProperty("user.dir");
        return rootPath + File.separator +  "pom.xml";
    }

    private static String parsePropertyTokens(String s) {
        final String OPEN = "${"; //$NON-NLS-1$
        final String CLOSE = "}"; //$NON-NLS-1$
        int currentIndex = 0;

        List<String> answer = new ArrayList<>();

        int markerStartIndex = s.indexOf(OPEN);
        if (markerStartIndex < 0) {
            // no parameter markers
            answer.add(s);
            currentIndex = s.length();
        }

        while (markerStartIndex > -1) {
            if (markerStartIndex > currentIndex) {
                // add the characters before the next parameter marker
                answer.add(s.substring(currentIndex, markerStartIndex));
                currentIndex = markerStartIndex;
            }

            int markerEndIndex = s.indexOf(CLOSE, currentIndex);
            int nestedStartIndex = s.indexOf(OPEN, markerStartIndex + OPEN.length());
            while (nestedStartIndex > -1 && markerEndIndex > -1 && nestedStartIndex < markerEndIndex) {
                nestedStartIndex = s.indexOf(OPEN, nestedStartIndex + OPEN.length());
                markerEndIndex = s.indexOf(CLOSE, markerEndIndex + CLOSE.length());
            }

            if (markerEndIndex < 0) {
                // no closing delimiter, just move to the end of the string
                answer.add(s.substring(markerStartIndex));
                currentIndex = s.length();
                break;
            }

            // we have a valid property marker...
            String property = s.substring(markerStartIndex + OPEN.length(), markerEndIndex);
            String propertyValue = resolveProperty(parsePropertyTokens(property));
            if (propertyValue == null) {
                // add the property marker back into the stream
                answer.add(s.substring(markerStartIndex, markerEndIndex + 1));
            } else {
                answer.add(propertyValue);
            }

            currentIndex = markerEndIndex + CLOSE.length();
            markerStartIndex = s.indexOf(OPEN, currentIndex);
        }

        if (currentIndex < s.length()) {
            answer.add(s.substring(currentIndex));
        }

        return String.join("", answer);
    }

    private static String resolveProperty(String key) {
        String property = null;
        // todo 可进行扩展
        property = System.getProperty(key);

        return property;
    }
}
