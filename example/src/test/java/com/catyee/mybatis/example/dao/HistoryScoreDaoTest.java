package com.catyee.mybatis.example.dao;

import com.catyee.mybatis.example.model.HistoryScore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class HistoryScoreDaoTest extends JUnitDaoWithFraud {

    @Autowired
    private HistoryScoreDao dao;

    private HistoryScore hsc;

    @Before
    public void setUp() throws Exception {
        hsc = dao.create(fraudHistoryScore());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void create() {
        HistoryScore hs = fraudHistoryScore();
        HistoryScore hscd = dao.create(hs);
        assertNotNull(hscd.getId());
    }

    @Test
    public void batchCreate() {
        List<HistoryScore> historyScores = fraudList(this::fraudHistoryScore);
        List<HistoryScore> hslcd = dao.batchCreate(historyScores);
        assertNotNull(hslcd);
        hslcd.forEach(historyScore -> assertNotNull(historyScore.getId()));
    }

    @Test
    public void update() {
        LocalDateTime newExamTime = LocalDateTime.now().minusDays(-5);
        hsc.setExamTime(newExamTime);
        HistoryScore hsu = dao.update(hsc);
        assertEquals(hsu.getExamTime(), newExamTime);
    }

    @Test
    public void delete() {
        dao.delete(hsc.getId());
        HistoryScore hsg = dao.get(hsc.getId());
        assertNull(hsg);
    }

    @Test
    public void deleteWithStudentId() {
        dao.deleteWithStudentId(hsc.getStudentId());
        List<HistoryScore> historyScores = dao.getAllWithStudentId(hsc.getStudentId(), 10, 0);
        assertTrue(historyScores.isEmpty());
    }

    @Test
    public void get() {
        HistoryScore hsg = dao.get(hsc.getId());
        assertNotNull(hsg);
        assertEquals(hsg.getId(), hsc.getId());
    }

    @Test
    public void getWithExamType() {
        List<HistoryScore> historyScores = dao.getWithExamType(hsc.getStudentId(), hsc.getExamType());
        assertTrue(historyScores.size() >= 1);
    }

    @Test
    public void getAllWithStudentId() {
        List<HistoryScore> historyScores = dao.getAllWithStudentId(hsc.getStudentId(), 10, 0);
        assertTrue(historyScores.size() >= 1);
    }
}