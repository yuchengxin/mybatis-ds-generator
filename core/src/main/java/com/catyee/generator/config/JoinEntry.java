package com.catyee.generator.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JoinEntry {
    private String leftTable;
    private String targetProject;
    private String targetPackage;
    private List<Pair<String, JoinTarget>> details;

    public static String getJoinResultMapId(String javaTableName) {
        return "Join" + javaTableName + "Result";
    }

    public void validate() {
        if (isEmpty(leftTable)) {
            throw new RuntimeException("The left table participating in join operation cannot be empty");
        }
        if (isEmpty(targetProject)) {
            throw new RuntimeException("The target project path to store xml mapper cannot be empty");
        }
        if (isEmpty(targetPackage)) {
            throw new RuntimeException("The target package path to store xml mapper cannot be empty");
        }
        if (details == null || details.isEmpty()) {
            throw new RuntimeException("The join target table detail cannot be empty");
        }
        for (Pair<String, JoinTarget> detail : details) {
            String leftTableColumn = detail.getLeft();
            if (isEmpty(leftTableColumn)) {
                throw new RuntimeException("The column of left table participating in join operation cannot be empty");
            }
            detail.getRight().validate();
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
