package com.catyee.generator.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomTypeDetail {
    private String columnName;
    private String javaType;
    private String javaProperty;
    private String jdbcType;
    private String typeHandler;
    private boolean isDelimitedColumnName;
    private List<String> genericTypes;
}
