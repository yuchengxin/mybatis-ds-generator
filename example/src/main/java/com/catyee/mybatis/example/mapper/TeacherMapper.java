package com.catyee.mybatis.example.mapper;

import static com.catyee.mybatis.example.mapper.TeacherDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import com.catyee.mybatis.example.custom.handler.StringListHandler;
import com.catyee.mybatis.example.model.Teacher;
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
public interface TeacherMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, name, cardNum, gender, birthday, workSeniority, techCourses, createTime, lastUpdateTime);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<Teacher> insertStatement);

    @Insert({
        "${insertStatement}"
    })
    @Options(useGeneratedKeys=true,keyProperty="records.id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<Teacher> records);

    default int insertMultiple(MultiRowInsertStatementProvider<Teacher> multipleInsertStatement) {
        return insertMultiple(multipleInsertStatement.getInsertStatement(), multipleInsertStatement.getRecords());
    }

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TeacherResult")
    Optional<Teacher> selectOne(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="TeacherResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="card_num", property="cardNum", jdbcType=JdbcType.VARCHAR),
        @Result(column="gender", property="gender", jdbcType=JdbcType.VARCHAR),
        @Result(column="birthday", property="birthday", jdbcType=JdbcType.DATE),
        @Result(column="work_seniority", property="workSeniority", jdbcType=JdbcType.INTEGER),
        @Result(column="tech_courses", property="techCourses", typeHandler=StringListHandler.class, jdbcType=JdbcType.VARCHAR),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="last_update_time", property="lastUpdateTime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<Teacher> selectMany(SelectStatementProvider selectStatement);

    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, teacher, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, teacher, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(Teacher record) {
        return MyBatis3Utils.insert(this::insert, record, teacher, c ->
            c.map(name).toProperty("name")
            .map(cardNum).toProperty("cardNum")
            .map(gender).toProperty("gender")
            .map(birthday).toProperty("birthday")
            .map(workSeniority).toProperty("workSeniority")
            .map(techCourses).toProperty("techCourses")
        );
    }

    default int insertMultiple(Collection<Teacher> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, teacher, c ->
            c.map(name).toProperty("name")
            .map(cardNum).toProperty("cardNum")
            .map(gender).toProperty("gender")
            .map(birthday).toProperty("birthday")
            .map(workSeniority).toProperty("workSeniority")
            .map(techCourses).toProperty("techCourses")
        );
    }

    default int insertSelective(Teacher record) {
        return MyBatis3Utils.insert(this::insert, record, teacher, c ->
            c.map(name).toPropertyWhenPresent("name", record::getName)
            .map(cardNum).toPropertyWhenPresent("cardNum", record::getCardNum)
            .map(gender).toPropertyWhenPresent("gender", record::getGender)
            .map(birthday).toPropertyWhenPresent("birthday", record::getBirthday)
            .map(workSeniority).toPropertyWhenPresent("workSeniority", record::getWorkSeniority)
            .map(techCourses).toPropertyWhenPresent("techCourses", record::getTechCourses)
        );
    }

    default Optional<Teacher> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, teacher, completer);
    }

    default List<Teacher> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, teacher, completer);
    }

    default List<Teacher> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, teacher, completer);
    }

    default Optional<Teacher> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, teacher, completer);
    }

    static UpdateDSL<UpdateModel> updateAllColumns(Teacher record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(name).equalTo(record::getName)
                .set(cardNum).equalTo(record::getCardNum)
                .set(gender).equalTo(record::getGender)
                .set(birthday).equalTo(record::getBirthday)
                .set(workSeniority).equalTo(record::getWorkSeniority)
                .set(techCourses).equalTo(record::getTechCourses);
    }

    static UpdateDSL<UpdateModel> updateSelectiveColumns(Teacher record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(name).equalToWhenPresent(record::getName)
                .set(cardNum).equalToWhenPresent(record::getCardNum)
                .set(gender).equalToWhenPresent(record::getGender)
                .set(birthday).equalToWhenPresent(record::getBirthday)
                .set(workSeniority).equalToWhenPresent(record::getWorkSeniority)
                .set(techCourses).equalToWhenPresent(record::getTechCourses);
    }

    default int updateByPrimaryKey(Teacher record) {
        return update(c ->
            c.set(name).equalTo(record::getName)
            .set(cardNum).equalTo(record::getCardNum)
            .set(gender).equalTo(record::getGender)
            .set(birthday).equalTo(record::getBirthday)
            .set(workSeniority).equalTo(record::getWorkSeniority)
            .set(techCourses).equalTo(record::getTechCourses)
            .where(id, isEqualTo(record::getId))
        );
    }

    default int updateByPrimaryKeySelective(Teacher record) {
        return update(c ->
            c.set(name).equalToWhenPresent(record::getName)
            .set(cardNum).equalToWhenPresent(record::getCardNum)
            .set(gender).equalToWhenPresent(record::getGender)
            .set(birthday).equalToWhenPresent(record::getBirthday)
            .set(workSeniority).equalToWhenPresent(record::getWorkSeniority)
            .set(techCourses).equalToWhenPresent(record::getTechCourses)
            .where(id, isEqualTo(record::getId))
        );
    }
}