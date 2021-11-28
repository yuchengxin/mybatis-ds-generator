DROP TABLE IF EXISTS `exam_class_grade`;
DROP TABLE IF EXISTS `exam_teacher`;
DROP TABLE IF EXISTS `exam_student`;
DROP TABLE IF EXISTS `exam_history_score`;

CREATE TABLE IF NOT EXISTS `exam_class_grade`
(
    `id`               BIGINT(10)   NOT NULL    AUTO_INCREMENT,
    `name`             VARCHAR(100) NOT NULL,                                /* 班级名 */
    `description`      VARCHAR(255) NULL        DEFAULT NULL,                /* 班级描述 */
    `grade_type`       VARCHAR(20)  NOT NULL,                                /* 班级类型：上午班、下午班、全天班 */
    `regulator_id`     BIGINT(10)   NOT NULL,                                /* 班主任的id */
    `create_time`      DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    `last_update_time` DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_exam_class_grade_name` (`name`)
)
    ENGINE = InnoDB
    CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `exam_teacher`
(
    `id`               BIGINT(10)   NOT NULL    AUTO_INCREMENT,
    `name`             VARCHAR(100) NOT NULL,                                 /* 教师名 */
    `card_num`         VARCHAR(50)  NOT NULL    UNIQUE,                       /* 身份证号，唯一键 */
    `gender`           VARCHAR(20)  NOT NULL,                                 /* 性别 */
    `birthday`         DATE         NULL        DEFAULT NULL,                 /* 生日 */
    `work_seniority`   INT          NOT NULL,                                 /* 教龄 */
    `tech_courses`     VARCHAR(255) NULL        DEFAULT NULL,                 /* 教授的课程列表，双逗号分割 */
    `create_time`      DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    `last_update_time` DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_exam_teacher_name` (`name`)
)
    ENGINE = InnoDB
    CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `exam_student`
(
    `id`               BIGINT(10)   NOT NULL    AUTO_INCREMENT,
    `grade_id`         BIGINT(10)   NOT NULL,                                  /* 班级id */
    `name`             VARCHAR(100) NOT NULL,                                  /* 学生姓名 */
    `card_num`         VARCHAR(50)  NOT NULL    UNIQUE,                        /* 身份证号，唯一键 */
    `gender`           VARCHAR(20)  NOT NULL,                                  /* 性别 */
    `birthday`         DATE         NULL        DEFAULT NULL,                  /* 生日 */
    `take_courses`     VARCHAR(255) NOT NULL,                                  /* 报名的课程列表 */
    `from_foreign`     BOOLEAN      NOT NULL,                                  /* 是否是国外学生 */
    `hometown`         VARCHAR(100) NULL        DEFAULT NULL,                  /* 家乡 */
    `hobbies`          TEXT         NULL        DEFAULT NULL,                  /* 兴趣列表，存储的是json字符串 */
    `create_time`      DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    `last_update_time` DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_exam_student_grade_id` (`grade_id`)
)
    ENGINE = InnoDB
    CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `exam_history_score`
(
    `id`               BIGINT(10)   NOT NULL    AUTO_INCREMENT,
    `student_id`       BIGINT(10)   NOT NULL,                                  /* 学生id */
    `exam_time`        DATETIME     NOT NULL,                                  /* 考试时间 */
    `exam_type`        VARCHAR(20)  NOT NULL,                                  /* 考试类型 */
    `total_score`      INT(10)      NOT NULL,                                  /* 总分 */
    `score`            TEXT         NOT NULL,                                  /* 科目成绩详情 */
    `create_time`      DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    `last_update_time` DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_exam_history_score_st_id` (`student_id`)
)
    ENGINE = InnoDB
    CHARSET = utf8;