package com.catyee.mybatis.example.mapper;

import com.catyee.mybatis.example.custom.entity.ExamType;
import java.sql.JDBCType;
import java.time.LocalDateTime;
import java.util.Map;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class HistoryScoreDynamicSqlSupport {
    public static final HistoryScore historyScore = new HistoryScore();

    public static final SqlColumn<Long> id = historyScore.id;

    public static final SqlColumn<Long> studentId = historyScore.studentId;

    public static final SqlColumn<LocalDateTime> examTime = historyScore.examTime;

    public static final SqlColumn<ExamType> examType = historyScore.examType;

    public static final SqlColumn<Integer> totalScore = historyScore.totalScore;

    public static final SqlColumn<LocalDateTime> createTime = historyScore.createTime;

    public static final SqlColumn<LocalDateTime> lastUpdateTime = historyScore.lastUpdateTime;

    public static final SqlColumn<Map<String, Integer>> score = historyScore.score;

    public static final class HistoryScore extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<Long> studentId = column("student_id", JDBCType.BIGINT);

        public final SqlColumn<LocalDateTime> examTime = column("exam_time", JDBCType.TIMESTAMP);

        public final SqlColumn<ExamType> examType = column("exam_type", JDBCType.VARCHAR);

        public final SqlColumn<Integer> totalScore = column("total_score", JDBCType.INTEGER);

        public final SqlColumn<LocalDateTime> createTime = column("create_time", JDBCType.TIMESTAMP);

        public final SqlColumn<LocalDateTime> lastUpdateTime = column("last_update_time", JDBCType.TIMESTAMP);

        public final SqlColumn<Map<String, Integer>> score = column("score", JDBCType.CLOB, "com.catyee.mybatis.example.custom.handler.ScoreMapHandler");

        public HistoryScore() {
            super("exam_history_score");
        }
    }
}