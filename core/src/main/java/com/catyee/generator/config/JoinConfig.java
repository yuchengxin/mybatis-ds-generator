package com.catyee.generator.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinConfig {
    private String targetPackage;
    private String targetProject;
    private Map<String, JoinEntry> joinDetailMap;

    public JoinEntry getJoinEntry(String tableName) {
        JoinEntry joinEntry = joinDetailMap.get(tableName);
        if (joinEntry != null) {
            joinEntry.setTargetPackage(targetPackage);
            joinEntry.setTargetProject(targetProject);
        }
        return joinEntry;
    }
}
