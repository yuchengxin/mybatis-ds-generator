package com.catyee.mybatis.example.mapper;

import static com.catyee.mybatis.example.mapper.HistoryScoreDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import com.catyee.mybatis.example.custom.handler.ScoreMapHandler;
import com.catyee.mybatis.example.model.HistoryScore;
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
public interface HistoryScoreMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, studentId, examTime, examType, totalScore, createTime, lastUpdateTime, score);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<HistoryScore> insertStatement);

    @Insert({
        "${insertStatement}"
    })
    @Options(useGeneratedKeys=true,keyProperty="records.id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<HistoryScore> records);

    default int insertMultiple(MultiRowInsertStatementProvider<HistoryScore> multipleInsertStatement) {
        return insertMultiple(multipleInsertStatement.getInsertStatement(), multipleInsertStatement.getRecords());
    }

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("HistoryScoreResult")
    Optional<HistoryScore> selectOne(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="HistoryScoreResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="student_id", property="studentId", jdbcType=JdbcType.BIGINT),
        @Result(column="exam_time", property="examTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="exam_type", property="examType", jdbcType=JdbcType.VARCHAR),
        @Result(column="total_score", property="totalScore", jdbcType=JdbcType.INTEGER),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="last_update_time", property="lastUpdateTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="score", property="score", typeHandler=ScoreMapHandler.class, jdbcType=JdbcType.CLOB)
    })
    List<HistoryScore> selectMany(SelectStatementProvider selectStatement);

    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, historyScore, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, historyScore, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(HistoryScore record) {
        return MyBatis3Utils.insert(this::insert, record, historyScore, c ->
            c.map(studentId).toProperty("studentId")
            .map(examTime).toProperty("examTime")
            .map(examType).toProperty("examType")
            .map(totalScore).toProperty("totalScore")
            .map(score).toProperty("score")
        );
    }

    default int insertMultiple(Collection<HistoryScore> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, historyScore, c ->
            c.map(studentId).toProperty("studentId")
            .map(examTime).toProperty("examTime")
            .map(examType).toProperty("examType")
            .map(totalScore).toProperty("totalScore")
            .map(score).toProperty("score")
        );
    }

    default int insertSelective(HistoryScore record) {
        return MyBatis3Utils.insert(this::insert, record, historyScore, c ->
            c.map(studentId).toPropertyWhenPresent("studentId", record::getStudentId)
            .map(examTime).toPropertyWhenPresent("examTime", record::getExamTime)
            .map(examType).toPropertyWhenPresent("examType", record::getExamType)
            .map(totalScore).toPropertyWhenPresent("totalScore", record::getTotalScore)
            .map(score).toPropertyWhenPresent("score", record::getScore)
        );
    }

    default Optional<HistoryScore> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, historyScore, completer);
    }

    default List<HistoryScore> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, historyScore, completer);
    }

    default List<HistoryScore> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, historyScore, completer);
    }

    default Optional<HistoryScore> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, historyScore, completer);
    }

    static UpdateDSL<UpdateModel> updateAllColumns(HistoryScore record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(studentId).equalTo(record::getStudentId)
                .set(examTime).equalTo(record::getExamTime)
                .set(examType).equalTo(record::getExamType)
                .set(totalScore).equalTo(record::getTotalScore)
                .set(score).equalTo(record::getScore);
    }

    static UpdateDSL<UpdateModel> updateSelectiveColumns(HistoryScore record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(studentId).equalToWhenPresent(record::getStudentId)
                .set(examTime).equalToWhenPresent(record::getExamTime)
                .set(examType).equalToWhenPresent(record::getExamType)
                .set(totalScore).equalToWhenPresent(record::getTotalScore)
                .set(score).equalToWhenPresent(record::getScore);
    }

    default int updateByPrimaryKey(HistoryScore record) {
        return update(c ->
            c.set(studentId).equalTo(record::getStudentId)
            .set(examTime).equalTo(record::getExamTime)
            .set(examType).equalTo(record::getExamType)
            .set(totalScore).equalTo(record::getTotalScore)
            .set(score).equalTo(record::getScore)
            .where(id, isEqualTo(record::getId))
        );
    }

    default int updateByPrimaryKeySelective(HistoryScore record) {
        return update(c ->
            c.set(studentId).equalToWhenPresent(record::getStudentId)
            .set(examTime).equalToWhenPresent(record::getExamTime)
            .set(examType).equalToWhenPresent(record::getExamType)
            .set(totalScore).equalToWhenPresent(record::getTotalScore)
            .set(score).equalToWhenPresent(record::getScore)
            .where(id, isEqualTo(record::getId))
        );
    }
}