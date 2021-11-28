package com.catyee.mybatis.example.custom.mapper;

import com.catyee.generator.utils.MyBatis3CustomUtils;
import com.catyee.mybatis.example.model.Student;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

import java.util.Collection;
import java.util.List;

import static com.catyee.mybatis.example.mapper.StudentDynamicSqlSupport.*;
import static com.catyee.mybatis.example.mapper.StudentDynamicSqlSupport.hobbies;

public interface StudentCustomMapper {

    @InsertProvider(type= SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int ignoreInsert(InsertStatementProvider<Student> insertStatement);

    @Insert({
            "${insertStatement}"
    })
    @Options(useGeneratedKeys=true,keyProperty="records.id")
    int ignoreInsertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<Student> records);

    default int ignoreInsertMultiple(MultiRowInsertStatementProvider<Student> multipleInsertStatement) {
        return ignoreInsertMultiple(multipleInsertStatement.getInsertStatement(), multipleInsertStatement.getRecords());
    }


    /**
     * 示例： 如果数据库中已有记录则不再插入，没有则插入，判断的依据是表的主键或唯一键
     * 最终生成的sql语句是insert ignore into，使用的时候要注意数据库是否支持该语法
     *
     * 返回的是插入的条数，如果是0表示没有插入成功，如果是1则插入成功
     *
     * 注意：对于mysql，即使未插入也会使自增id增长
     */
    default int ignoreInsert(Student record) {
        return MyBatis3CustomUtils.ignoreInsert(this::ignoreInsert, record, student, c ->
                c.map(gradeId).toProperty("gradeId")
                        .map(name).toProperty("name")
                        .map(cardNum).toProperty("cardNum")
                        .map(gender).toProperty("gender")
                        .map(birthday).toProperty("birthday")
                        .map(takeCourses).toProperty("takeCourses")
                        .map(fromForeign).toProperty("fromForeign")
                        .map(hometown).toProperty("hometown")
                        .map(hobbies).toProperty("hobbies")
        );
    }

    /**
     * 示例： 如果数据库中已有记录则不再插入，没有则插入，判断的依据是表的主键或唯一键
     * 最终生成的sql语句是insert ignore into，使用的时候要注意数据库是否支持该语法
     *
     * 返回的是插入的条数，如果是0则没有插入任何一条
     *
     * 注意：对于mysql，即使未插入也会使自增id增长
     */
    default int ignoreInsertMultiple(Collection<Student> records) {
        return MyBatis3CustomUtils.ignoreInsertMultiple(this::ignoreInsertMultiple, records, student, c ->
                c.map(gradeId).toProperty("gradeId")
                        .map(name).toProperty("name")
                        .map(cardNum).toProperty("cardNum")
                        .map(gender).toProperty("gender")
                        .map(birthday).toProperty("birthday")
                        .map(takeCourses).toProperty("takeCourses")
                        .map(fromForeign).toProperty("fromForeign")
                        .map(hometown).toProperty("hometown")
                        .map(hobbies).toProperty("hobbies")
        );
    }
}
