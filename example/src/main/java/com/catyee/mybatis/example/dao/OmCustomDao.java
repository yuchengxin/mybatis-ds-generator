package com.catyee.mybatis.example.dao;

import com.catyee.mybatis.example.custom.entity.ExamType;
import com.catyee.mybatis.example.custom.mapper.OmCustomMapper;
import com.catyee.mybatis.example.custom.model.StudentExamScoreDetail;
import com.catyee.mybatis.example.mapper.HistoryScoreDynamicSqlSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

/**
 * 对应于自定义的Mapper, 用于实现更为复杂的业务
 */
@Repository
public class OmCustomDao {
    private final OmCustomMapper mapper;

    @Autowired
    public OmCustomDao(OmCustomMapper mapper) {
        this.mapper = mapper;
    }

    public List<StudentExamScoreDetail> getAllDetail() {
        return mapper.listStudentExamScoreDetail(c -> c);
    }

    public List<StudentExamScoreDetail> getStudentExamScoreDetail(long studentId) {
        return mapper.listStudentExamScoreDetail(c -> c.where(HistoryScoreDynamicSqlSupport.studentId, isEqualTo(studentId)));
    }

    public List<StudentExamScoreDetail> getStudentExamScoreDetail(ExamType type) {
        return mapper.listStudentExamScoreDetail(c -> c.where(HistoryScoreDynamicSqlSupport.examType, isEqualTo(type)));
    }
}
