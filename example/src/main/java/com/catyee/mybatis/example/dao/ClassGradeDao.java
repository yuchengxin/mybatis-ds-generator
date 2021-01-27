package com.catyee.mybatis.example.dao;

import com.catyee.mybatis.example.custom.entity.GradeType;
import com.catyee.mybatis.example.mapper.*;
import com.catyee.mybatis.example.model.ClassGrade;
import com.catyee.mybatis.example.model.Student;
import com.catyee.mybatis.example.model.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Repository
public class ClassGradeDao {

    private final ClassGradeMapper gradeMapper;

    private final StudentMapper studentMapper;

    private final TeacherMapper teacherMapper;

    @Autowired
    public ClassGradeDao(ClassGradeMapper gradeMapper, StudentMapper studentMapper, TeacherMapper teacherMapper) {
        this.gradeMapper = gradeMapper;
        this.studentMapper = studentMapper;
        this.teacherMapper = teacherMapper;
    }

    @Transactional
    public ClassGrade create(ClassGrade grade) {
        Teacher regulator = grade.getRegulator();
        if (regulator != null) {
            teacherMapper.insert(regulator);
            grade.setRegulatorId(regulator.getId());
        }
        gradeMapper.insert(grade);
        List<Student> students = grade.getStudents();
        if (students != null && !students.isEmpty()) {
            students.forEach(student -> student.setGradeId(grade.getId()));
            studentMapper.insertMultiple(students);
        }
        return grade;
    }

    @Transactional
    public List<ClassGrade> batchCreate(List<ClassGrade> grades) {
        List<Teacher> regulators = new ArrayList<>();
        for (ClassGrade grade : grades) {
            Teacher regulator = grade.getRegulator();
            Optional.ofNullable(regulator).ifPresent(regulators::add);
        }
        teacherMapper.insertMultiple(regulators);
        grades.forEach(grade -> grade.setRegulatorId(grade.getRegulator().getId()));

        gradeMapper.insertMultiple(grades);
        List<Student> allStudents = new ArrayList<>();
        for (ClassGrade grade : grades) {
            List<Student> students = grade.getStudents();
            if (students != null && !students.isEmpty()) {
                students.forEach(student -> student.setGradeId(grade.getId()));
                allStudents.addAll(students);
            }

        }
        studentMapper.insertMultiple(allStudents);
        return grades;
    }

    /**
     * 只更新部分内容，返回的实体和传入的实体可能会不一样，返回的是从数据库中查询的结果
     *
     * @param grade
     * @return
     */
    public ClassGrade updateOnly(ClassGrade grade) {
        gradeMapper.updateByPrimaryKey(grade);
        return get(grade.getId());
    }

    @Transactional
    public ClassGrade updateAll(ClassGrade grade) {
        Teacher regulator = grade.getRegulator();
        if (regulator != null) {
            if (regulator.getId() != null) {
                teacherMapper.updateByPrimaryKey(regulator);
            } else {
                teacherMapper.insert(regulator);
            }
            grade.setRegulatorId(regulator.getId());
        }
        gradeMapper.updateByPrimaryKey(grade);
        List<Student> students = grade.getStudents();
        if (students != null && !students.isEmpty()) {
            students.forEach(student -> student.setGradeId(grade.getId()));
            studentMapper.delete(c -> c.where(StudentDynamicSqlSupport.gradeId, isEqualTo(grade.getId())));
            studentMapper.insertMultiple(students);
        }
        return grade;
    }

    @Transactional
    public void delete(long id) {
        gradeMapper.deleteByPrimaryKey(id);
        studentMapper.delete(c -> c.where(StudentDynamicSqlSupport.gradeId, isEqualTo(id)));
    }

    // 查询join结果的时候要使用leftJoin对应的方法
    // 另外由于join的特殊性，不能使用limit进行物理分页，否则可能得到错误的结果
    public ClassGrade get(long id) {
        return gradeMapper.leftJoinSelectOne(c -> c.where(ClassGradeDynamicSqlSupport.id, isEqualTo(id))).orElse(null);
    }

    public List<ClassGrade> getWithGradeType(GradeType type) {
        return gradeMapper.leftJoinSelect(c -> c.where(ClassGradeDynamicSqlSupport.gradeType, isEqualTo(type)));
    }

    public List<ClassGrade> getWithRegulatorId(long regulatorId) {
        return gradeMapper.leftJoinSelect(c -> c.where(ClassGradeDynamicSqlSupport.regulatorId, isEqualTo(regulatorId)));
    }

}
