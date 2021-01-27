package com.catyee.generator.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class JoinTarget {
    private String rightTable;
    private String filedName;
    private String joinColumn;
    private JoinType type;

    public enum JoinType {
        ONE,
        MORE,
        ;
    }

    public void validate() {
        if (isEmpty(rightTable)) {
            throw new RuntimeException("The right table participating in join operation cannot be empty");
        }
        if (isEmpty(filedName)) {
            throw new RuntimeException("The filed name of join result cannot be empty");
        }
        if (isEmpty(joinColumn)) {
            throw new RuntimeException("The column of right table participating in join operation cannot be empty");
        }
        if (type == null) {
            throw new RuntimeException("Join type[ONE/MORE] cannot be empty");
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}

