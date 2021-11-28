package com.catyee.mybatis.example.dao;

import com.catyee.mybatis.example.custom.mapper.StudentCustomMapper;
import com.catyee.mybatis.example.mapper.StudentDynamicSqlSupport;
import com.catyee.mybatis.example.mapper.StudentMapper;
import com.catyee.mybatis.example.model.Student;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.catyee.mybatis.example.mapper.StudentDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Repository
public class StudentDao {

    private final StudentMapper mapper;
    private final StudentCustomMapper customMapper;

    @Autowired
    public StudentDao(StudentMapper mapper, StudentCustomMapper customMapper) {
        this.mapper = mapper;
        this.customMapper = customMapper;
    }

    public Student create(Student student) {
        mapper.insert(student);
        return student;
    }

    public List<Student> batchCreate(List<Student> students) {
        mapper.insertMultiple(students);
        return students;
    }

    /**
     * 演示 insert ignore into用法
     * @param student
     * @return
     */
    public boolean createIfNotExist(Student student) {
        int count = customMapper.ignoreInsert(student);
        return count > 0;
    }

    /**
     * 演示 insert ignore into用法
     * @param students
     * @return
     */
    public int batchCreateIfNotExist(List<Student> students) {
        return customMapper.ignoreInsertMultiple(students);
    }

    public Student update(Student student) {
        mapper.updateByPrimaryKey(student);
        return student;
    }

    /**
     * 演示只更新一部分的写法
     *
     * 只更新部分内容，返回的实体和传入的实体可能会不一样，返回的是从数据库中查询的结果
     *
     * @param student
     * @return
     */
    public Student updatePart(Student student) {
        UpdateStatementProvider updateStatement = SqlBuilder.update(StudentDynamicSqlSupport.student)
                .set(name).equalTo(student.getName())
                .set(gender).equalTo(student.getGender())
                .set(birthday).equalTo(student.getBirthday())
                .set(fromForeign).equalTo(student.getFromForeign())
                .set(hometown).equalToWhenPresent(student.getHometown())
                .where(id, isEqualTo(student.getId()))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        mapper.update(updateStatement);
        return get(student.getId());
    }

    public void delete(long id) {
        mapper.deleteByPrimaryKey(id);
    }

    public void deleteWithGradeId(long gradeId) {
        mapper.delete(c -> c.where(StudentDynamicSqlSupport.gradeId, isEqualTo(gradeId)));
    }

    public Student get(long id) {
        return mapper.selectByPrimaryKey(id).orElse(null);
    }

    public List<Student> getWithName(long gradeId, String name) {
        return mapper.select(c -> c.where(StudentDynamicSqlSupport.gradeId, isEqualTo(gradeId))
                .and(StudentDynamicSqlSupport.name, isEqualTo(name))
        );
    }

    public List<Student> getForeignStudents() {
        return mapper.select(c -> c.where(fromForeign, isEqualTo(true)));
    }

    public List<Student> getBirthBeforeDate(LocalDate birth) {
        return mapper.select(c -> c.where(birthday, isLessThan(birth)));
    }
}
