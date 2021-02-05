package com.catyee.mybatis.example.custom.model;

import com.catyee.mybatis.example.custom.entity.ExamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentExamScoreDetail {
    private int total;
    private Long studentId;
    private ExamType examType;
}
