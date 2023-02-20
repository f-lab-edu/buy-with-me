CREATE TABLE IF NOT EXISTS `address`
(
    `address_id` bigint NOT NULL AUTO_INCREMENT,
    `depth1`     varchar(255),
    `depth2`     varchar(255),
    `depth3`     varchar(255),
    PRIMARY KEY (`address_id`),
    UNIQUE KEY `UniqueAddress` (`depth1`, `depth2`, `depth3`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `member`
(
    `member_id`  bigint NOT NULL AUTO_INCREMENT,
    `login_id`   varchar(255),
    `name`       varchar(255),
    `password`   varchar(255),
    `phone_no`   varchar(255),
    `address_id` bigint,
    PRIMARY KEY (`member_id`),
    UNIQUE KEY (`login_id`),
    FOREIGN KEY (`address_id`) REFERENCES `address` (`address_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `post`
(
    `post_id`    bigint NOT NULL AUTO_INCREMENT,
    `content`    text,
    `created_at` datetime(6),
    `current_no` int    NOT NULL,
    `expiration` datetime(6),
    `status`     varchar(255),
    `target_no`  int    NOT NULL,
    `title`      varchar(255),
    `address_id` bigint,
    `member_id`  bigint,
    PRIMARY KEY (`post_id`),
    FULLTEXT KEY `ft_index` (`title`, `content`) WITH PARSER `ngram`,
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
    FOREIGN KEY (`address_id`) REFERENCES `address` (`address_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `post_comment`
(
    `comment_id` bigint NOT NULL AUTO_INCREMENT,
    `content`    varchar(255),
    `created_at` datetime(6),
    `updated_at` datetime(6),
    `member_id`  bigint,
    `post_id`    bigint,
    `parent_id`  bigint,
    PRIMARY KEY (`comment_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
    FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`),
    FOREIGN KEY (`parent_id`) REFERENCES `post_comment` (`comment_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `enroll`
(
    `enroll_id`  bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6),
    `member_id`  bigint,
    `post_id`    bigint,
    PRIMARY KEY (`enroll_id`),
    UNIQUE KEY (`member_id`, `post_id`),
    FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `notification`
(
    `notification_id`   bigint NOT NULL AUTO_INCREMENT,
    `checked`           bit(1) NOT NULL,
    `notification_type` varchar(255),
    `member_id`         bigint,
    `form_url`          varchar(255),
    `created_at`        datetime(6),
    `updated_at`        datetime(6),
    PRIMARY KEY (`notification_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `member_evaluation`
(
    `evaluation_id` bigint NOT NULL AUTO_INCREMENT,
    `post_id`       bigint,
    `member_id`     bigint,
    `colleague_id`  bigint,
    `created_at`    datetime(6),
    `content`       varchar(255),
    PRIMARY KEY (`evaluation_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
    FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`),
    FOREIGN KEY (`colleague_id`) REFERENCES `member` (`member_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;