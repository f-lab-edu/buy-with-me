INSERT INTO address (depth1, depth2, depth3)
VALUES ("성남시", "분당구", "판교동");

INSERT INTO member (login_id, name, password, phone_no, address_id)
VALUES ("test", "kim", "1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014",
        "010-1111-1111", 1);

INSERT INTO post (title, content, created_at, expiration, status, current_no, target_no, address_id,
                  member_id)
VALUES ("test 게시물", "test 목적으로 생성하였음", "2023-02-01 12:00:00", "2023-04-04 23:00:00", "RUNNING", 0,
        100, 1, 1);

INSERT INTO post (title, content, created_at, expiration, status, current_no, target_no, address_id,
                  member_id)
VALUES ("검색용", "test 목적으로 생성하였음", "2023-04-04 12:00:00", "2023-04-04 23:00:00", "RUNNING", 0, 100,
        1, 1);

INSERT INTO post (title, content, created_at, expiration, status, current_no, target_no, address_id,
                  member_id)
VALUES ("test 게시물", "검색용", "2018-02-01 12:00:00", "2023-04-04 23:00:00", "RUNNING", 0, 100, 1, 1);

INSERT INTO enroll (created_at, member_id, post_id)
VALUES ("2023-02-01 00:00:00", 1, 1);

INSERT INTO post_comment (post_id, member_id, content)
VALUES (1, 1, "test comment");

INSERT INTO post_comment (post_id, member_id, content, parent_id)
VALUES (1, 1, "test sub comment", 1);