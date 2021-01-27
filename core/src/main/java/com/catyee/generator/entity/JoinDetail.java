package com.catyee.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlTable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JoinDetail {
    private BasicColumn leftTableJoinColumn;
    private SqlTable rightJoinTable;
    private BasicColumn rightTableJoinColumn;

    public static JoinDetail of(BasicColumn leftTableJoinColumn, SqlTable rightJoinTable, BasicColumn rightTableJoinColumn) {
        return JoinDetail.builder()
                .leftTableJoinColumn(leftTableJoinColumn)
                .rightJoinTable(rightJoinTable)
                .rightTableJoinColumn(rightTableJoinColumn)
                .build();
    }
}
