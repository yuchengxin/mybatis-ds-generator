package com.catyee.generator.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomTypeConfig {
    private Map<String, CustomTypeEntry> customTypeDetailMap;

    public CustomTypeEntry getCustomTypeDetail(String column) {
        return customTypeDetailMap.get(column);
    }
}
