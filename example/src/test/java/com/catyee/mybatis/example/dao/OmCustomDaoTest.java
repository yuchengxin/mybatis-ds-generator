package com.catyee.mybatis.example.dao;

import com.catyee.mybatis.example.custom.entity.ExamType;
import com.catyee.mybatis.example.custom.model.StudentExamScoreDetail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class OmCustomDaoTest extends JUnitDaoWithFraud {

    @Autowired
    private HistoryScoreDao scoreDao;

    @Autowired
    private OmCustomDao omDao;

    @Before
    public void setUp() throws Exception {
        scoreDao.create(fraudHistoryScore(1));
        scoreDao.batchCreate(fraudList(() -> fraudHistoryScore(fraudRandom(1, 5))));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getAllDetail() {
        List<StudentExamScoreDetail> all = omDao.getAllDetail();
        assertTrue(all.size() > 0);
    }

    @Test
    public void getStudentExamScoreDetail() {
        List<StudentExamScoreDetail> details = omDao.getStudentExamScoreDetail(1);
        assertTrue(details.size() > 0);
    }

    @Test
    public void testGetStudentExamScoreDetail() {
        List<StudentExamScoreDetail> details = omDao.getStudentExamScoreDetail(ExamType.FINAL_EXAM);
        assertTrue(details.size() > 0);
    }
}