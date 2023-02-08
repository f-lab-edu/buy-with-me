package com.flab.buywithme.repository;

import static com.flab.buywithme.TestFixture.fakePageable;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestInstance(Lifecycle.PER_CLASS)
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private DataSource dataSource;

    private final Long memberId = 1L;
    private final Pageable pageable = fakePageable();

    @BeforeAll
    public void beforeAll() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/sql/data.sql"));
        }
    }

    @AfterAll
    public void afterAll() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/sql/truncate.sql"));
        }
    }

    @Test
    @DisplayName("개별 회원의 알림 데이터 조회 성공")
    void findAllByMember_id() {
        Long wrongMemberId = 99L;
        assertEquals(3,
                notificationRepository.findAllByMember_id(memberId, pageable).getTotalElements());
        assertEquals(2, notificationRepository.findAllByMember_id(memberId, pageable)
                .getTotalPages()); //알림 개수: 3 페이지 크기: 2
        assertEquals(0, notificationRepository.findAllByMember_id(wrongMemberId, pageable)
                .getTotalElements());
    }

    @Test
    @DisplayName("개별 회원의 안 읽은 알림 개수 조회 성공")
    void countByMember_IdAndChecked() {
        int notCheckedNoti = notificationRepository.countByMember_IdAndCheckedFalse(memberId);
        assertEquals(2, notCheckedNoti);
    }
}