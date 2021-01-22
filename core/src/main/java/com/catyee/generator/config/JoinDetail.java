package com.catyee.generator.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JoinDetail {
    private String tableName;
    private String targetProject;
    private String targetPackage;
    private String joinColumn;
    private List<JoinTarget> joinTargets;

    public static String getJoinResultMapId(String javaTableName) {
        return "Join" + javaTableName + "Result";
    }
}
