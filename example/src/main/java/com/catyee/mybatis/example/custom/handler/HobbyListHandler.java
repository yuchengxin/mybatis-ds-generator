package com.catyee.mybatis.example.custom.handler;

import com.catyee.mybatis.example.custom.entity.Hobby;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HobbyListHandler extends BaseTypeHandler<List<Hobby>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Hobby> hobbies, JdbcType jdbcType) throws SQLException {
        ps.setString(i, list2string(hobbies));
    }

    @Override
    public List<Hobby> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String r = rs.getString(columnName);
        if (rs.wasNull())
            return null;
        return string2list(r);
    }

    @Override
    public List<Hobby> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String r = rs.getString(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return string2list(r);
    }

    @Override
    public List<Hobby> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String r = cs.getString(columnIndex);
        if (cs.wasNull()) {
            return new ArrayList<>();
        }
        return string2list(r);
    }

    private String list2string(List<Hobby> list) throws SQLException {
        try {
            if (list == null || list.isEmpty()) {
                return null;
            }
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new SQLException("Error when serialize hobbies", e);
        }
    }

    private List<Hobby> string2list(String str) throws SQLException {
        try {
            if (str == null || str.isEmpty()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(str, new TypeReference<List<Hobby>>() {

            });
        } catch (Exception e) {
            throw new SQLException("Error when deserialize hobbies", e);
        }
    }
}
