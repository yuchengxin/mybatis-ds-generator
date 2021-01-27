package com.catyee.mybatis.example.mapper;

import static com.catyee.mybatis.example.mapper.ClassGradeDynamicSqlSupport.*;
import static com.catyee.mybatis.example.mapper.StudentDynamicSqlSupport.student;
import static com.catyee.mybatis.example.mapper.TeacherDynamicSqlSupport.teacher;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import com.catyee.generator.entity.JoinDetail;
import com.catyee.generator.utils.MyBatis3CustomUtils;
import com.catyee.mybatis.example.model.ClassGrade;
import java.util.ArrayList;
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
import org.mybatis.dynamic.sql.SqlTable;
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
public interface ClassGradeMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, name, description, gradeType, regulatorId, createTime, lastUpdateTime);

    BasicColumn[] leftJoinSelectList = BasicColumn.columnList(id, name, description, gradeType, regulatorId, createTime, lastUpdateTime,
		(student.id).as("exam_student_id"), 
		(student.gradeId).as("exam_student_grade_id"), 
		(student.name).as("exam_student_name"), 
		(student.gender).as("exam_student_gender"), 
		(student.birthday).as("exam_student_birthday"), 
		(student.takeCourses).as("exam_student_take_courses"), 
		(student.fromForeign).as("exam_student_from_foreign"), 
		(student.hometown).as("exam_student_hometown"), 
		(student.createTime).as("exam_student_create_time"), 
		(student.lastUpdateTime).as("exam_student_last_update_time"), 
		(student.hobbies).as("exam_student_hobbies"), 
		(teacher.id).as("exam_teacher_id"), 
		(teacher.name).as("exam_teacher_name"), 
		(teacher.gender).as("exam_teacher_gender"), 
		(teacher.birthday).as("exam_teacher_birthday"), 
		(teacher.workSeniority).as("exam_teacher_work_seniority"), 
		(teacher.techCourses).as("exam_teacher_tech_courses"), 
		(teacher.createTime).as("exam_teacher_create_time"), 
		(teacher.lastUpdateTime).as("exam_teacher_last_update_time"));

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<ClassGrade> insertStatement);

    @Insert({
        "${insertStatement}"
    })
    @Options(useGeneratedKeys=true,keyProperty="records.id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<ClassGrade> records);

    default int insertMultiple(MultiRowInsertStatementProvider<ClassGrade> multipleInsertStatement) {
        return insertMultiple(multipleInsertStatement.getInsertStatement(), multipleInsertStatement.getRecords());
    }

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("ClassGradeResult")
    Optional<ClassGrade> selectOne(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="ClassGradeResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="description", property="description", jdbcType=JdbcType.VARCHAR),
        @Result(column="grade_type", property="gradeType", jdbcType=JdbcType.VARCHAR),
        @Result(column="regulator_id", property="regulatorId", jdbcType=JdbcType.BIGINT),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="last_update_time", property="lastUpdateTime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<ClassGrade> selectMany(SelectStatementProvider selectStatement);

    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, classGrade, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, classGrade, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(ClassGrade record) {
        return MyBatis3Utils.insert(this::insert, record, classGrade, c ->
            c.map(name).toProperty("name")
            .map(description).toProperty("description")
            .map(gradeType).toProperty("gradeType")
            .map(regulatorId).toProperty("regulatorId")
        );
    }

    default int insertMultiple(Collection<ClassGrade> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, classGrade, c ->
            c.map(name).toProperty("name")
            .map(description).toProperty("description")
            .map(gradeType).toProperty("gradeType")
            .map(regulatorId).toProperty("regulatorId")
        );
    }

    default int insertSelective(ClassGrade record) {
        return MyBatis3Utils.insert(this::insert, record, classGrade, c ->
            c.map(name).toPropertyWhenPresent("name", record::getName)
            .map(description).toPropertyWhenPresent("description", record::getDescription)
            .map(gradeType).toPropertyWhenPresent("gradeType", record::getGradeType)
            .map(regulatorId).toPropertyWhenPresent("regulatorId", record::getRegulatorId)
        );
    }

    default Optional<ClassGrade> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, classGrade, completer);
    }

    default List<ClassGrade> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, classGrade, completer);
    }

    default List<ClassGrade> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, classGrade, completer);
    }

    default Optional<ClassGrade> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, classGrade, completer);
    }

    static UpdateDSL<UpdateModel> updateAllColumns(ClassGrade record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(name).equalTo(record::getName)
                .set(description).equalTo(record::getDescription)
                .set(gradeType).equalTo(record::getGradeType)
                .set(regulatorId).equalTo(record::getRegulatorId);
    }

    static UpdateDSL<UpdateModel> updateSelectiveColumns(ClassGrade record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(name).equalToWhenPresent(record::getName)
                .set(description).equalToWhenPresent(record::getDescription)
                .set(gradeType).equalToWhenPresent(record::getGradeType)
                .set(regulatorId).equalToWhenPresent(record::getRegulatorId);
    }

    default int updateByPrimaryKey(ClassGrade record) {
        return update(c ->
            c.set(name).equalTo(record::getName)
            .set(description).equalTo(record::getDescription)
            .set(gradeType).equalTo(record::getGradeType)
            .set(regulatorId).equalTo(record::getRegulatorId)
            .where(id, isEqualTo(record::getId))
        );
    }

    default int updateByPrimaryKeySelective(ClassGrade record) {
        return update(c ->
            c.set(name).equalToWhenPresent(record::getName)
            .set(description).equalToWhenPresent(record::getDescription)
            .set(gradeType).equalToWhenPresent(record::getGradeType)
            .set(regulatorId).equalToWhenPresent(record::getRegulatorId)
            .where(id, isEqualTo(record::getId))
        );
    }

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("JoinClassGradeResult")
    Optional<ClassGrade> leftJoinSelectOne(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("JoinClassGradeResult")
    List<ClassGrade> leftJoinSelectMany(SelectStatementProvider selectStatement);

    default List<ClassGrade> leftJoinSelect(SelectDSLCompleter completer) {
        List<JoinDetail> joinDetails = new ArrayList<>();
        joinDetails.add(JoinDetail.of(id, student, student.gradeId));
        joinDetails.add(JoinDetail.of(regulatorId, teacher, teacher.id));
        return MyBatis3CustomUtils.leftJoinSelectList(this::leftJoinSelectMany, leftJoinSelectList, classGrade, joinDetails , completer);
    }

    default Optional<ClassGrade> leftJoinSelectOne(SelectDSLCompleter completer) {
        List<JoinDetail> joinDetails = new ArrayList<>();
        joinDetails.add(JoinDetail.of(id, student, student.gradeId));
        joinDetails.add(JoinDetail.of(regulatorId, teacher, teacher.id));
        return MyBatis3CustomUtils.leftJoinSelectOne(this::leftJoinSelectOne, leftJoinSelectList, classGrade, joinDetails , completer);
    }
}