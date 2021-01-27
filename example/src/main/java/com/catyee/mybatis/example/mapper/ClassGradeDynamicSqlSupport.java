package com.catyee.mybatis.example.mapper;

import com.catyee.mybatis.example.custom.entity.GradeType;
import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ClassGradeDynamicSqlSupport {
    public static final ClassGrade classGrade = new ClassGrade();

    public static final SqlColumn<Long> id = classGrade.id;

    public static final SqlColumn<String> name = classGrade.name;

    public static final SqlColumn<String> description = classGrade.description;

    public static final SqlColumn<GradeType> gradeType = classGrade.gradeType;

    public static final SqlColumn<Long> regulatorId = classGrade.regulatorId;

    public static final SqlColumn<LocalDateTime> createTime = classGrade.createTime;

    public static final SqlColumn<LocalDateTime> lastUpdateTime = classGrade.lastUpdateTime;

    public static final class ClassGrade extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> name = column("`name`", JDBCType.VARCHAR);

        public final SqlColumn<String> description = column("description", JDBCType.VARCHAR);

        public final SqlColumn<GradeType> gradeType = column("grade_type", JDBCType.VARCHAR);

        public final SqlColumn<Long> regulatorId = column("regulator_id", JDBCType.BIGINT);

        public final SqlColumn<LocalDateTime> createTime = column("create_time", JDBCType.TIMESTAMP);

        public final SqlColumn<LocalDateTime> lastUpdateTime = column("last_update_time", JDBCType.TIMESTAMP);

        public ClassGrade() {
            super("exam_class_grade");
        }
    }
}