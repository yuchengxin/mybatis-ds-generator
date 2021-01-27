package com.catyee.mybatis.example.model;

import com.catyee.mybatis.example.custom.entity.GradeType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassGrade implements Serializable {
    private Long id;

    private String name;

    private String description;

    private GradeType gradeType;

    private Long regulatorId;

    private LocalDateTime createTime;

    private LocalDateTime lastUpdateTime;

    private List<Student> students;

    private Teacher regulator;

    private static final long serialVersionUID = 1L;
}