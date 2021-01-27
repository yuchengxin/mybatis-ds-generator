package com.catyee.mybatis.example.dao;

import com.catyee.generator.utils.MyBatis3CustomUtils;
import com.catyee.mybatis.example.mapper.TeacherDynamicSqlSupport;
import com.catyee.mybatis.example.mapper.TeacherMapper;
import com.catyee.mybatis.example.model.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.catyee.mybatis.example.mapper.TeacherDynamicSqlSupport.workSeniority;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThan;

@Repository
public class TeacherDao {
    private final TeacherMapper mapper;

    @Autowired
    public TeacherDao(TeacherMapper mapper) {
        this.mapper = mapper;
    }

    public Teacher create(Teacher teacher) {
        mapper.insert(teacher);
        return teacher;
    }

    public List<Teacher> batchCreate(List<Teacher> teachers) {
        mapper.insertMultiple(teachers);
        return teachers;
    }

    public Teacher update(Teacher teacher) {
        mapper.updateByPrimaryKey(teacher);
        return teacher;
    }

    public void delete(long id) {
        mapper.deleteByPrimaryKey(id);
    }

    public Teacher get(long id) {
        return mapper.selectByPrimaryKey(id).orElse(null);
    }

    public List<Teacher> getWithName(String name) {
        return mapper.select(c -> c.where(TeacherDynamicSqlSupport.name, isEqualTo(name)));
    }

    public List<Teacher> getAll(Long limit, Long offset) {
        return mapper.select(c -> MyBatis3CustomUtils.buildLimitOffset(c, limit, offset));
    }

    public List<Teacher> getGreatWorkSeniorityTeacher(int seniority, Long limit, Long offset) {
        return mapper.select(c -> {
            c.where(workSeniority, isGreaterThan(seniority));
            return MyBatis3CustomUtils.buildLimitOffset(c, limit, offset);
        });
    }
}
