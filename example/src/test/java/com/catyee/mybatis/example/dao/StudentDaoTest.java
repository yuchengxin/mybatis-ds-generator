package com.catyee.mybatis.example.dao;

import com.catyee.mybatis.example.custom.entity.Hobby;
import com.catyee.mybatis.example.model.Student;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class StudentDaoTest extends JUnitDaoWithFraud {

    @Autowired
    private StudentDao dao;

    private Student sc;

    @Before
    public void setUp() throws Exception {
        sc = dao.create(fraudStudent());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void create() {
        Student tsc = fraudStudent();
        Student scd = dao.create(tsc);
        assertNotNull(scd);
        assertNotNull(scd.getId());
    }

    @Test
    public void batchCreate() {
        List<Student> students = fraudList(this::fraudStudent);
        List<Student> scList = dao.batchCreate(students);
        assertNotNull(scList);
        scList.forEach(student -> {
            assertNotNull(student);
            assertNotNull(student.getId());
        });
    }

    @Test
    public void update() {
        String newHomeTown = fraudUnique("new_town");
        LocalDate newBirthday = LocalDate.now().plusYears(-17);
        sc.setBirthday(newBirthday);
        sc.setHometown(newHomeTown);
        List<Hobby> newHobbies = fraudList(super::fraudHobby);
        sc.setHobbies(newHobbies);
        Student su = dao.update(sc);
        Student sg = dao.get(sc.getId());
        assertNotNull(su);
        assertNotNull(sg);
        assertEquals(sg.getBirthday(), newBirthday);
        assertEquals(sg.getHometown(), newHomeTown);
        assertEquals(sg.getHobbies().size(), newHobbies.size());
        assertEquals(sg.getHobbies().get(0).getName(), newHobbies.get(0).getName());
    }

    @Test
    public void delete() {
        dao.delete(sc.getId());
        Student sg = dao.get(sc.getId());
        assertNull(sg);
    }

    @Test
    public void deleteWithGradeId() {
        dao.deleteWithGradeId(sc.getGradeId());
        Student sg = dao.get(sc.getId());
        assertNull(sg);
    }

    @Test
    public void get() {
        Student sg = dao.get(sc.getId());
        assertNotNull(sg);
        assertEquals(sg.getId(), sc.getId());
    }

    @Test
    public void getWithName() {
        List<Student> students = dao.getWithName(sc.getGradeId(), sc.getName());
        assertNotNull(students);
        assertEquals(students.get(0).getName(), sc.getName());
    }

    @Test
    public void getForeignStudents() {
        Student foreign = fraudStudent();
        foreign.setFromForeign(true);
        dao.create(foreign);
        List<Student> foreigns = dao.getForeignStudents();
        assertNotNull(foreigns);
        assertTrue(foreigns.size() >= 1);
    }

    @Test
    public void getBirthBeforeDate() {
        List<Student> students = dao.getBirthBeforeDate(sc.getBirthday().plusYears(1));
        assertNotNull(students);
        assertEquals(students.get(0).getName(), sc.getName());
        List<Student> stl = dao.getBirthBeforeDate(sc.getBirthday().plusYears(-1));
        assertTrue(stl.isEmpty());
    }

    @Test
    public void updatePart() {
        String newHomeTown = "this is new hometown";
        sc.setHometown(newHomeTown);
        Student scg = dao.updatePart(sc);
        assertNotNull(scg.getHometown());
        assertEquals(scg.getHometown(), newHomeTown);
    }
}