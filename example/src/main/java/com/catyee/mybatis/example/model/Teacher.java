package com.catyee.mybatis.example.model;

import com.catyee.mybatis.example.custom.entity.Gender;
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
public class Teacher implements Serializable {
    private Long id;

    private String name;

    private String cardNum;

    private Gender gender;

    private LocalDate birthday;

    private Integer workSeniority;

    private List<String> techCourses;

    private LocalDateTime createTime;

    private LocalDateTime lastUpdateTime;

    private static final long serialVersionUID = 1L;
}