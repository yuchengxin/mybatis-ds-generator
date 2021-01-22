package com.catyee.mybatis.example.custom.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoreMapHandler extends BaseTypeHandler<Map<String, Integer>> {

    private static final String KEY_VALUE_DELIMITER = "::";
    private static final String KV_PAIR_DELIMITER = ";;";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, Integer> stringIntegerMap, JdbcType jdbcType) throws SQLException {
        ps.setString(i, map2String(stringIntegerMap));
    }

    @Override
    public Map<String, Integer> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String r = rs.getString(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return string2Map(r);
    }

    @Override
    public Map<String, Integer> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String r = rs.getString(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return string2Map(r);
    }

    @Override
    public Map<String, Integer> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String r = cs.getString(columnIndex);
        if (cs.wasNull()) {
            return new HashMap<>();
        }
        return string2Map(r);
    }

    private String map2String(Map<String,Integer> scores) {
        if (scores == null || scores.isEmpty()) {
            return null;
        }
        return scores.entrySet().stream().map(entry -> entry.getKey() + KEY_VALUE_DELIMITER + entry.getValue())
                .collect(Collectors.joining(KV_PAIR_DELIMITER));
    }

    private Map<String, Integer> string2Map(String str) {
        Map<String, Integer> result = new HashMap<>();
        if (str == null || str.isEmpty()) {
            return result;
        }
        String[] kvs = str.split(KV_PAIR_DELIMITER);
        for (String kv : kvs) {
            String[] keyValue = kv.split(KEY_VALUE_DELIMITER);
            result.put(keyValue[0], Integer.parseInt(keyValue[1]));
        }
        return result;
    }

}
