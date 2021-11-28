package com.catyee.mybatis.example.mapper;

import com.catyee.mybatis.example.custom.entity.Gender;
import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class TeacherDynamicSqlSupport {
    public static final Teacher teacher = new Teacher();

    public static final SqlColumn<Long> id = teacher.id;

    public static final SqlColumn<String> name = teacher.name;

    public static final SqlColumn<String> cardNum = teacher.cardNum;

    public static final SqlColumn<Gender> gender = teacher.gender;

    public static final SqlColumn<LocalDate> birthday = teacher.birthday;

    public static final SqlColumn<Integer> workSeniority = teacher.workSeniority;

    public static final SqlColumn<List<String>> techCourses = teacher.techCourses;

    public static final SqlColumn<LocalDateTime> createTime = teacher.createTime;

    public static final SqlColumn<LocalDateTime> lastUpdateTime = teacher.lastUpdateTime;

    public static final class Teacher extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> name = column("`name`", JDBCType.VARCHAR);

        public final SqlColumn<String> cardNum = column("card_num", JDBCType.VARCHAR);

        public final SqlColumn<Gender> gender = column("gender", JDBCType.VARCHAR);

        public final SqlColumn<LocalDate> birthday = column("birthday", JDBCType.DATE);

        public final SqlColumn<Integer> workSeniority = column("work_seniority", JDBCType.INTEGER);

        public final SqlColumn<List<String>> techCourses = column("tech_courses", JDBCType.VARCHAR, "com.catyee.mybatis.example.custom.handler.StringListHandler");

        public final SqlColumn<LocalDateTime> createTime = column("create_time", JDBCType.TIMESTAMP);

        public final SqlColumn<LocalDateTime> lastUpdateTime = column("last_update_time", JDBCType.TIMESTAMP);

        public Teacher() {
            super("exam_teacher");
        }
    }
}