package com.catyee.mybatis.example.dao;

import com.catyee.mybatis.example.custom.entity.ExamType;
import com.catyee.mybatis.example.custom.entity.Gender;
import com.catyee.mybatis.example.custom.entity.GradeType;
import com.catyee.mybatis.example.custom.entity.Hobby;
import com.catyee.mybatis.example.model.ClassGrade;
import com.catyee.mybatis.example.model.HistoryScore;
import com.catyee.mybatis.example.model.Student;
import com.catyee.mybatis.example.model.Teacher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

public class JUnitDaoWithFraud extends JUnitDaoBase {

    public ClassGrade fraudClassGrade() {
        return ClassGrade.builder()
                .name(fraudUnique("class"))
                .description("this is desc of class")
                .gradeType(fraudRandom(0, 1) == 0 ? GradeType.WHOLE_DAY : GradeType.AFTERNOON)
                .regulator(fraudTeacher())
                .students(fraudList(this::fraudStudent))
                .build();
    }

    public Student fraudStudent(long gradeId) {
        return Student.builder()
                .name(fraudUnique("student"))
                .gender(Gender.FEMALE)
                .birthday(LocalDate.now().plusYears(-18))
                .fromForeign(fraudRandom(0, 1) != 0)
                .gradeId(gradeId)
                .hobbies(fraudList(this::fraudHobby))
                .hometown(fraudUnique("town"))
                .takeCourses(fraudStringList("course"))
                .build();
    }

    public Student fraudStudent() {
        return Student.builder()
                .name(fraudUnique("student"))
                .gender(Gender.FEMALE)
                .birthday(LocalDate.now().plusYears(-18))
                .fromForeign(fraudRandom(0, 1) != 0)
                .gradeId(1L)
                .hobbies(fraudList(this::fraudHobby))
                .hometown(fraudUnique("town"))
                .takeCourses(fraudStringList("course"))
                .build();
    }

    public Teacher fraudTeacher() {
        return Teacher.builder()
                .name(fraudUnique("teacher"))
                .gender(Gender.MALE)
                .birthday(LocalDate.now().plusYears(-25))
                .techCourses(fraudStringList("course"))
                .workSeniority(fraudRandom(1, 6))
                .build();
    }

    public HistoryScore fraudHistoryScore(long studentId) {
        Map<String, Integer> scores = fraudScore();
        int totalScore = scores.values().stream().mapToInt(s -> s).sum();
        return HistoryScore.builder()
                .studentId(studentId)
                .examType(ExamType.FINAL_EXAM)
                .examTime(LocalDateTime.now().plusMonths(-5))
                .score(scores)
                .totalScore(totalScore)
                .build();
    }

    public HistoryScore fraudHistoryScore() {
        Map<String, Integer> scores = fraudScore();
        int totalScore = scores.values().stream().mapToInt(s -> s).sum();
        return HistoryScore.builder()
                .studentId(1L)
                .examType(ExamType.FINAL_EXAM)
                .examTime(LocalDateTime.now().plusMonths(-5))
                .score(scores)
                .totalScore(totalScore)
                .build();
    }

    public Hobby fraudHobby() {
        return Hobby.builder()
                .name(fraudUnique("hobby"))
                .desc("this is desc of hobby")
                .build();
    }

    public Map<String, Integer> fraudScore() {
        Map<String, Integer> scores = new HashMap<>();
        int end = fraudRandom(2, 5);
        for (int i = 0; i < end; i++) {
            scores.put(fraudCourse(), fraudRandom(90, 150));
        }
        return scores;
    }

    public <T> List<T> fraudList(Supplier<T> supplier) {
        List<T> list = new ArrayList<>();
        int end = fraudRandom(2, 5);
        for (int i = 0; i < end; i++) {
            list.add(supplier.get());
        }
        return list;
    }

    public List<String> fraudStringList(String str) {
        List<String> list = new ArrayList<>();
        int end = fraudRandom(2, 5);
        for (int i = 0; i < end; i++) {
            list.add(fraudUnique(str));
        }
        return list;
    }

    public String fraudCourse() {
        return fraudUnique("course");
    }

    public String fraudUnique(String str) {
        return str + "_" + UUID.randomUUID().toString().replace("-", "");
    }

    public Integer fraudRandom(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

}
