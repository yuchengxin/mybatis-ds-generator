DROP TABLE IF EXISTS `exam_class_grade`;
DROP TABLE IF EXISTS `exam_teacher`;
DROP TABLE IF EXISTS `exam_student`;
DROP TABLE IF EXISTS `exam_history_score`;

CREATE TABLE IF NOT EXISTS `exam_class_grade`
(
    `id`               BIGINT(10)   NOT NULL    AUTO_INCREMENT,
    `name`             VARCHAR(100) NOT NULL,
    `description`      VARCHAR(255) NULL        DEFAULT NULL,
    `grade_type`       VARCHAR(20)  NOT NULL,
    `regulator_id`     BIGINT(10)   NOT NULL,
    `create_time`      DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    `last_update_time` DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_exam_class_grade_name` (`name`)
)
    ENGINE = InnoDB
    CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `exam_teacher`
(
    `id`               BIGINT(10)   NOT NULL    AUTO_INCREMENT,
    `name`             VARCHAR(100) NOT NULL,
    `gender`           VARCHAR(20)  NOT NULL,
    `birthday`         DATE         NULL        DEFAULT NULL,
    `work_seniority`   INT          NOT NULL,
    `tech_courses`     VARCHAR(255) NULL        DEFAULT NULL,
    `create_time`      DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    `last_update_time` DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_exam_teacher_name` (`name`)
)
    ENGINE = InnoDB
    CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `exam_student`
(
    `id`               BIGINT(10)   NOT NULL    AUTO_INCREMENT,
    `grade_id`         BIGINT(10)   NOT NULL,
    `name`             VARCHAR(100) NOT NULL,
    `gender`           VARCHAR(20)  NOT NULL,
    `birthday`         DATE         NULL        DEFAULT NULL,
    `take_courses`     VARCHAR(255) NOT NULL,
    `from_foreign`     BOOLEAN      NOT NULL,
    `hometown`         VARCHAR(100) NULL        DEFAULT NULL,
    `hobbies`          TEXT         NULL        DEFAULT NULL,
    `create_time`      DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    `last_update_time` DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_exam_student_grade_id` (`grade_id`)
)
    ENGINE = InnoDB
    CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `exam_history_score`
(
    `id`               BIGINT(10)   NOT NULL    AUTO_INCREMENT,
    `student_id`       BIGINT(10)   NOT NULL,
    `exam_time`        DATETIME     NOT NULL,
    `exam_type`        VARCHAR(20)  NOT NULL,
    `total_score`      INT(10)      NOT NULL,
    `score`            TEXT         NOT NULL,
    `create_time`      DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    `last_update_time` DATETIME     NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_exam_history_score_st_id` (`student_id`)
)
    ENGINE = InnoDB
    CHARSET = utf8;