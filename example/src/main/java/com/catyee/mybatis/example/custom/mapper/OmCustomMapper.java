package com.catyee.mybatis.example.custom.mapper;

import com.catyee.generator.utils.MyBatis3CustomUtils;
import com.catyee.mybatis.example.custom.model.StudentExamScoreDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

import java.util.List;

import static com.catyee.mybatis.example.mapper.HistoryScoreDynamicSqlSupport.*;

/**
 * 自定义Mapper, 当自动生成的Mapper无法满足需求的时候(比如groupBy)也可以定制化开发自己的Mapper
 */
@Mapper
public interface OmCustomMapper {
    BasicColumn[] studentExamScoreDetailList = BasicColumn.columnList(studentId, examType, SqlBuilder.sum(totalScore).as("total"));
    BasicColumn[] studentExamScoreDetailGroupByList = BasicColumn.columnList(studentId, examType);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "StudentExamScoreDetailResult", value = {
            @Result(column = "student_id", property = "studentId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "exam_type", property = "examType", jdbcType = JdbcType.VARCHAR),
            @Result(column = "total", property = "total", jdbcType = JdbcType.INTEGER),
    })
    List<StudentExamScoreDetail> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    // 示例: group by的实现方式
    default List<StudentExamScoreDetail> listStudentExamScoreDetail(SelectDSLCompleter completer) {
        return MyBatis3CustomUtils.groupBySelectList(this::selectMany, studentExamScoreDetailList, historyScore, studentExamScoreDetailGroupByList, null, completer);
    }

    // 示例: 计算select语句查询结果的数量
    default long count(SelectDSLCompleter completer) {
        SelectStatementProvider selectStatementProvider = MyBatis3CustomUtils.groupBySelect(studentExamScoreDetailList, historyScore, studentExamScoreDetailGroupByList, null, completer);
        return MyBatis3CustomUtils.countFromSelectQuery(this::count, selectStatementProvider);
    }
}