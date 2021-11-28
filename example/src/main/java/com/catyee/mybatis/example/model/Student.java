package com.catyee.mybatis.example.model;

import com.catyee.mybatis.example.custom.entity.Gender;
import com.catyee.mybatis.example.custom.entity.Hobby;
import java.io.Serializable;
import java.time.LocalDate;
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
public class Student implements Serializable {
    private Long id;

    private Long gradeId;

    private String name;

    private String cardNum;

    private Gender gender;

    private LocalDate birthday;

    private List<String> takeCourses;

    private Boolean fromForeign;

    private String hometown;

    private LocalDateTime createTime;

    private LocalDateTime lastUpdateTime;

    private List<Hobby> hobbies;

    private static final long serialVersionUID = 1L;
}