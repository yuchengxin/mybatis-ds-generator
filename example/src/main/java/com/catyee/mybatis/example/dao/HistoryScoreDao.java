package com.catyee.mybatis.example.dao;

import com.catyee.mybatis.example.custom.entity.ExamType;
import com.catyee.mybatis.example.mapper.HistoryScoreDynamicSqlSupport;
import com.catyee.mybatis.example.mapper.HistoryScoreMapper;
import com.catyee.mybatis.example.model.HistoryScore;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.catyee.mybatis.example.mapper.HistoryScoreDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Repository
public class HistoryScoreDao {
    private final HistoryScoreMapper mapper;

    @Autowired
    public HistoryScoreDao(HistoryScoreMapper mapper) {
        this.mapper = mapper;
    }

    public HistoryScore create(HistoryScore score) {
        mapper.insert(score);
        return score;
    }

    public List<HistoryScore> batchCreate(List<HistoryScore> scores) {
        mapper.insertMultiple(scores);
        return scores;
    }

    public HistoryScore update(HistoryScore scores) {
        mapper.updateByPrimaryKey(scores);
        return scores;
    }

    // 示例:定制化更新的实现方式
    public HistoryScore updateScore(long scoreId, Map<String, Integer> scores) {
        int totalScore = scores.values().stream().mapToInt(s -> s).sum();
        UpdateStatementProvider updateStatementProvider = UpdateDSL.update(historyScore)
                .set(score).equalTo(scores)
                .set(HistoryScoreDynamicSqlSupport.totalScore).equalTo(totalScore)
                .where(id, isEqualTo(scoreId))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        mapper.update(updateStatementProvider);
        return mapper.selectByPrimaryKey(scoreId).orElse(null);
    }

    public void delete(long id) {
        mapper.deleteByPrimaryKey(id);
    }

    public void deleteWithStudentId(long studentId) {
        mapper.delete(c -> c.where(HistoryScoreDynamicSqlSupport.studentId, isEqualTo(studentId)));
    }

    public HistoryScore get(long id) {
        return mapper.selectByPrimaryKey(id).orElse(null);
    }

    public List<HistoryScore> getWithExamType(long studentId, ExamType type) {
        return mapper.select(c -> c.where(HistoryScoreDynamicSqlSupport.studentId, isEqualTo(studentId))
                .and(examType, isEqualTo(type))
        );
    }

    public List<HistoryScore> getAllWithStudentId(long studentId, long limit, long offset) {
        return mapper.select(c -> c.where(HistoryScoreDynamicSqlSupport.studentId, isEqualTo(studentId))
                .limit(limit).offset(offset)
        );
    }
}
