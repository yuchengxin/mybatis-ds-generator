package com.catyee.generator.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomTypeEntry {
    private String columnName;
    private String javaType;
    private String javaProperty;
    private String jdbcType;
    private String typeHandler;
    private boolean isDelimitedColumnName;
    private List<String> genericTypes;

    public void validate() {
        if (isEmpty(columnName)) {
            throw new RuntimeException("Must select a column when using custom type feature.");
        }
        if (isEmpty(javaType)) {
            throw new RuntimeException("Must specify java type for selected column when using custom type feature");
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
