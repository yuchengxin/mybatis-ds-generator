package com.catyee.mybatis.example.mapper;

import com.catyee.mybatis.example.custom.entity.Gender;
import com.catyee.mybatis.example.custom.entity.Hobby;
import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class StudentDynamicSqlSupport {
    public static final Student student = new Student();

    public static final SqlColumn<Long> id = student.id;

    public static final SqlColumn<Long> gradeId = student.gradeId;

    public static final SqlColumn<String> name = student.name;

    public static final SqlColumn<String> cardNum = student.cardNum;

    public static final SqlColumn<Gender> gender = student.gender;

    public static final SqlColumn<LocalDate> birthday = student.birthday;

    public static final SqlColumn<List<String>> takeCourses = student.takeCourses;

    public static final SqlColumn<Boolean> fromForeign = student.fromForeign;

    public static final SqlColumn<String> hometown = student.hometown;

    public static final SqlColumn<LocalDateTime> createTime = student.createTime;

    public static final SqlColumn<LocalDateTime> lastUpdateTime = student.lastUpdateTime;

    public static final SqlColumn<List<Hobby>> hobbies = student.hobbies;

    public static final class Student extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<Long> gradeId = column("grade_id", JDBCType.BIGINT);

        public final SqlColumn<String> name = column("`name`", JDBCType.VARCHAR);

        public final SqlColumn<String> cardNum = column("card_num", JDBCType.VARCHAR);

        public final SqlColumn<Gender> gender = column("gender", JDBCType.VARCHAR);

        public final SqlColumn<LocalDate> birthday = column("birthday", JDBCType.DATE);

        public final SqlColumn<List<String>> takeCourses = column("take_courses", JDBCType.VARCHAR, "com.catyee.mybatis.example.custom.handler.StringListHandler");

        public final SqlColumn<Boolean> fromForeign = column("from_foreign", JDBCType.BOOLEAN);

        public final SqlColumn<String> hometown = column("hometown", JDBCType.VARCHAR);

        public final SqlColumn<LocalDateTime> createTime = column("create_time", JDBCType.TIMESTAMP);

        public final SqlColumn<LocalDateTime> lastUpdateTime = column("last_update_time", JDBCType.TIMESTAMP);

        public final SqlColumn<List<Hobby>> hobbies = column("hobbies", JDBCType.CLOB, "com.catyee.mybatis.example.custom.handler.HobbyListHandler");

        public Student() {
            super("exam_student");
        }
    }
}