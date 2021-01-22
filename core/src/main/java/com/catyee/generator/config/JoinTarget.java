package com.catyee.generator.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class JoinTarget {
    private String targetTable;
    private String filedName;
    private String joinColumn;
    private JoinType type;

    public enum JoinType {
        ONE,
        MORE,
        ;
    }
}

