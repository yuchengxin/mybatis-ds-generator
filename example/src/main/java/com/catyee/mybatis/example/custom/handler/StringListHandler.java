package com.catyee.mybatis.example.custom.handler;

import com.google.common.collect.Lists;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StringListHandler extends BaseTypeHandler<List<String>> {
    // 假设已经明确字符串中不会出现双逗号
    private static final String DELIMITER = ",,";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> list, JdbcType jdbcType) throws SQLException {
        ps.setString(i, list2string(list));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String r = rs.getString(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return string2list(r);
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String r = rs.getString(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return string2list(r);
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String r = cs.getString(columnIndex);
        if (cs.wasNull()) {
            return new ArrayList<>();
        }
        return string2list(r);
    }

    private String list2string(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size() - 1) {
                sb.append(DELIMITER);
            }
        }
        return sb.toString();
    }

    private List<String> string2list(String str) {
        if (str == null || str.isEmpty()) {
            return new ArrayList<>();
        }
        String[] ss = str.split(DELIMITER);
        return Lists.newArrayList(ss);
    }
}
