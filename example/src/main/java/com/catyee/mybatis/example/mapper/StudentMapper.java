package com.catyee.mybatis.example.mapper;

import static com.catyee.mybatis.example.mapper.StudentDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import com.catyee.mybatis.example.custom.handler.HobbyListHandler;
import com.catyee.mybatis.example.custom.handler.StringListHandler;
import com.catyee.mybatis.example.model.Student;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.select.CountDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface StudentMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, gradeId, name, gender, birthday, takeCourses, fromForeign, hometown, createTime, lastUpdateTime, hobbies);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<Student> insertStatement);

    @Insert({
        "${insertStatement}"
    })
    @Options(useGeneratedKeys=true,keyProperty="records.id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<Student> records);

    default int insertMultiple(MultiRowInsertStatementProvider<Student> multipleInsertStatement) {
        return insertMultiple(multipleInsertStatement.getInsertStatement(), multipleInsertStatement.getRecords());
    }

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("StudentResult")
    Optional<Student> selectOne(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="StudentResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="grade_id", property="gradeId", jdbcType=JdbcType.BIGINT),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="gender", property="gender", jdbcType=JdbcType.VARCHAR),
        @Result(column="birthday", property="birthday", jdbcType=JdbcType.DATE),
        @Result(column="take_courses", property="takeCourses", typeHandler=StringListHandler.class, jdbcType=JdbcType.VARCHAR),
        @Result(column="from_foreign", property="fromForeign", jdbcType=JdbcType.BOOLEAN),
        @Result(column="hometown", property="hometown", jdbcType=JdbcType.VARCHAR),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="last_update_time", property="lastUpdateTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="hobbies", property="hobbies", typeHandler=HobbyListHandler.class, jdbcType=JdbcType.CLOB)
    })
    List<Student> selectMany(SelectStatementProvider selectStatement);

    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, student, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, student, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(Student record) {
        return MyBatis3Utils.insert(this::insert, record, student, c ->
            c.map(gradeId).toProperty("gradeId")
            .map(name).toProperty("name")
            .map(gender).toProperty("gender")
            .map(birthday).toProperty("birthday")
            .map(takeCourses).toProperty("takeCourses")
            .map(fromForeign).toProperty("fromForeign")
            .map(hometown).toProperty("hometown")
            .map(hobbies).toProperty("hobbies")
        );
    }

    default int insertMultiple(Collection<Student> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, student, c ->
            c.map(gradeId).toProperty("gradeId")
            .map(name).toProperty("name")
            .map(gender).toProperty("gender")
            .map(birthday).toProperty("birthday")
            .map(takeCourses).toProperty("takeCourses")
            .map(fromForeign).toProperty("fromForeign")
            .map(hometown).toProperty("hometown")
            .map(hobbies).toProperty("hobbies")
        );
    }

    default int insertSelective(Student record) {
        return MyBatis3Utils.insert(this::insert, record, student, c ->
            c.map(gradeId).toPropertyWhenPresent("gradeId", record::getGradeId)
            .map(name).toPropertyWhenPresent("name", record::getName)
            .map(gender).toPropertyWhenPresent("gender", record::getGender)
            .map(birthday).toPropertyWhenPresent("birthday", record::getBirthday)
            .map(takeCourses).toPropertyWhenPresent("takeCourses", record::getTakeCourses)
            .map(fromForeign).toPropertyWhenPresent("fromForeign", record::getFromForeign)
            .map(hometown).toPropertyWhenPresent("hometown", record::getHometown)
            .map(hobbies).toPropertyWhenPresent("hobbies", record::getHobbies)
        );
    }

    default Optional<Student> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, student, completer);
    }

    default List<Student> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, student, completer);
    }

    default List<Student> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, student, completer);
    }

    default Optional<Student> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, student, completer);
    }

    static UpdateDSL<UpdateModel> updateAllColumns(Student record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gradeId).equalTo(record::getGradeId)
                .set(name).equalTo(record::getName)
                .set(gender).equalTo(record::getGender)
                .set(birthday).equalTo(record::getBirthday)
                .set(takeCourses).equalTo(record::getTakeCourses)
                .set(fromForeign).equalTo(record::getFromForeign)
                .set(hometown).equalTo(record::getHometown)
                .set(hobbies).equalTo(record::getHobbies);
    }

    static UpdateDSL<UpdateModel> updateSelectiveColumns(Student record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gradeId).equalToWhenPresent(record::getGradeId)
                .set(name).equalToWhenPresent(record::getName)
                .set(gender).equalToWhenPresent(record::getGender)
                .set(birthday).equalToWhenPresent(record::getBirthday)
                .set(takeCourses).equalToWhenPresent(record::getTakeCourses)
                .set(fromForeign).equalToWhenPresent(record::getFromForeign)
                .set(hometown).equalToWhenPresent(record::getHometown)
                .set(hobbies).equalToWhenPresent(record::getHobbies);
    }

    default int updateByPrimaryKey(Student record) {
        return update(c ->
            c.set(gradeId).equalTo(record::getGradeId)
            .set(name).equalTo(record::getName)
            .set(gender).equalTo(record::getGender)
            .set(birthday).equalTo(record::getBirthday)
            .set(takeCourses).equalTo(record::getTakeCourses)
            .set(fromForeign).equalTo(record::getFromForeign)
            .set(hometown).equalTo(record::getHometown)
            .set(hobbies).equalTo(record::getHobbies)
            .where(id, isEqualTo(record::getId))
        );
    }

    default int updateByPrimaryKeySelective(Student record) {
        return update(c ->
            c.set(gradeId).equalToWhenPresent(record::getGradeId)
            .set(name).equalToWhenPresent(record::getName)
            .set(gender).equalToWhenPresent(record::getGender)
            .set(birthday).equalToWhenPresent(record::getBirthday)
            .set(takeCourses).equalToWhenPresent(record::getTakeCourses)
            .set(fromForeign).equalToWhenPresent(record::getFromForeign)
            .set(hometown).equalToWhenPresent(record::getHometown)
            .set(hobbies).equalToWhenPresent(record::getHobbies)
            .where(id, isEqualTo(record::getId))
        );
    }
}