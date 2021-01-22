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
    private Map<String, JoinDetail> joinDetailMap;

    public JoinDetail getJoinDetail(String tableName) {
        JoinDetail joinDetail = joinDetailMap.get(tableName);
        if (joinDetail != null) {
            joinDetail.setTargetPackage(targetPackage);
            joinDetail.setTargetProject(targetProject);
        }
        return joinDetail;
    }
}
