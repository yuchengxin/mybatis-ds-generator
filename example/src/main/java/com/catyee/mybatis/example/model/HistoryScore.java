package com.catyee.mybatis.example.model;

import com.catyee.mybatis.example.custom.entity.ExamType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoryScore implements Serializable {
    private Long id;

    private Long studentId;

    private LocalDateTime examTime;

    private ExamType examType;

    private Integer totalScore;

    private LocalDateTime createTime;

    private LocalDateTime lastUpdateTime;

    private Map<String, Integer> score;

    private static final long serialVersionUID = 1L;
}