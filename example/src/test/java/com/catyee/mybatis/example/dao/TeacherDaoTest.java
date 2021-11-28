package com.catyee.mybatis.example.dao;

import com.catyee.mybatis.example.model.Teacher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class TeacherDaoTest extends JUnitDaoWithFraud {

    @Autowired
    private TeacherDao teacherDao;

    private Teacher tc;

    @Before
    public void setUp() throws Exception {
        tc = teacherDao.create(fraudTeacher());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void create() {
        Teacher ttc = fraudTeacher();
        Teacher ttcd = teacherDao.create(ttc);
        assertNotNull(ttcd.getId());
    }

    @Test
    public void batchCreate() {
        List<Teacher> list = fraudList(this::fraudTeacher);
        List<Teacher> listd = teacherDao.batchCreate(list);
        assertNotNull(listd);
        listd.forEach(teacher -> {
            assertNotNull(teacher);
            assertNotNull(teacher.getId());
        });
    }

    @Test
    public void update() {
        LocalDate newBirth = LocalDate.now().plusYears(-30);
        tc.setBirthday(newBirth);
        Teacher tcu = teacherDao.update(tc);
        assertEquals(tcu.getBirthday(), newBirth);
    }

    @Test
    public void delete() {
        teacherDao.delete(tc.getId());
        Teacher tcg = teacherDao.get(tc.getId());
        assertNull(tcg);
    }

    @Test
    public void get() {
        Teacher tcg = teacherDao.get(tc.getId());
        assertNotNull(tcg);
        assertEquals(tcg.getId(), tc.getId());
    }

    @Test
    public void getWithName() {
        List<Teacher> teachers = teacherDao.getWithName(tc.getName());
        assertTrue(teachers.size() >= 1);
        assertEquals(teachers.get(0).getName(), tc.getName());
    }

    @Test
    public void getAll() {
        List<Teacher> teachers = teacherDao.getAll(null, null);
        assertTrue(teachers.size() >= 1);
    }

    @Test
    public void getGreatWorkSeniorityTeacher() {
        List<Teacher> teachers = teacherDao.pageGreatWorkSeniorityTeacher(tc.getWorkSeniority() - 1,
                1, 10);
        assertTrue(teachers.size() >= 1);
        List<Teacher> ttls = teacherDao.pageGreatWorkSeniorityTeacher(tc.getWorkSeniority() + 1,
                null, null);
        assertTrue(ttls.isEmpty());
    }
}