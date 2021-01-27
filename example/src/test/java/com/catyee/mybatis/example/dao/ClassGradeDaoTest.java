package com.catyee.mybatis.example.dao;

import com.catyee.mybatis.example.model.ClassGrade;
import com.catyee.mybatis.example.model.Student;
import com.catyee.mybatis.example.model.Teacher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class ClassGradeDaoTest extends JUnitDaoWithFraud {

    @Autowired
    private ClassGradeDao dao;

    private ClassGrade grade;

    @Before
    public void setUp() throws Exception {
        grade = dao.create(fraudClassGrade());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void create() {
        ClassGrade tg = fraudClassGrade();
        ClassGrade tgd = dao.create(tg);
        assertNotNull(tgd);
        assertNotNull(tgd.getId());
        assertNotNull(tgd.getStudents());
        tgd.getStudents().forEach(student -> assertNotNull(student.getId()));
        assertNotNull(tgd.getRegulatorId());
        assertNotNull(tgd.getRegulator());
        assertNotNull(tgd.getRegulator().getId());
    }

    @Test
    public void batchCreate() {
        List<ClassGrade> classGrades = fraudList(this::fraudClassGrade);
        List<ClassGrade> classGradesCreated = dao.batchCreate(classGrades);
        assertNotNull(classGradesCreated);
        classGradesCreated.forEach(tgd -> {
            assertNotNull(tgd);
            assertNotNull(tgd.getId());
            assertNotNull(tgd.getStudents());
            tgd.getStudents().forEach(student -> assertNotNull(student.getId()));
            assertNotNull(tgd.getRegulatorId());
            assertNotNull(tgd.getRegulator());
            assertNotNull(tgd.getRegulator().getId());
        });
    }

    @Test
    public void updateOnly() {
        String newDesc = "this is new desc of class";
        grade.setDescription(newDesc);
        ClassGrade clu = dao.updateOnly(grade);
        assertEquals(clu.getDescription(), newDesc);
    }

    @Test
    public void updateAll() {
        String newDesc = "this is new desc of class";
        grade.setDescription(newDesc);
        List<Student> students = fraudList(this::fraudStudent);
        grade.setStudents(students);
        Teacher teacher = fraudTeacher();
        grade.setRegulator(teacher);
        dao.updateAll(grade);
        ClassGrade gradeGet = dao.get(grade.getId());
        assertNotNull(gradeGet);
        assertNotNull(gradeGet.getRegulatorId());
        assertNotNull(gradeGet.getRegulator());
        assertEquals(gradeGet.getRegulator().getId(), gradeGet.getRegulatorId());
        assertEquals(gradeGet.getRegulator().getName(), teacher.getName());
        assertNotNull(gradeGet.getStudents());
        assertEquals(gradeGet.getStudents().size(), students.size());
    }

    @Test
    public void delete() {
        dao.delete(grade.getId());
        ClassGrade gradeGet = dao.get(grade.getId());
        assertNull(gradeGet);
    }

    @Test
    public void get() {
        ClassGrade gradeGet = dao.get(grade.getId());
        assertNotNull(gradeGet);
        assertNotNull(gradeGet.getRegulatorId());
        assertNotNull(gradeGet.getRegulator());
        assertEquals(gradeGet.getRegulator().getId(), gradeGet.getRegulatorId());
        assertEquals(gradeGet.getRegulator().getId(), grade.getRegulatorId());
        assertEquals(gradeGet.getRegulator().getName(), grade.getRegulator().getName());
        assertNotNull(gradeGet.getStudents());
        assertEquals(gradeGet.getStudents().size(), grade.getStudents().size());
    }

    @Test
    public void getWithGradeType() {
        List<ClassGrade> grades = dao.getWithGradeType(grade.getGradeType());
        assertNotNull(grades);
        assertTrue(grades.size() >= 1);
    }

    @Test
    public void getWithRegulatorId() {
        List<ClassGrade> grades = dao.getWithRegulatorId(grade.getRegulatorId());
        assertNotNull(grades);
        assertTrue(grades.size() >= 1);
    }
}