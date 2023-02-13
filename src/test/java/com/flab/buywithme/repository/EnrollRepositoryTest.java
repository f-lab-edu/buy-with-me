package com.flab.buywithme.repository;

import static com.flab.buywithme.TestFixture.fakeEnroll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.flab.buywithme.config.JasyptConfig;
import com.flab.buywithme.config.JpaAuditingConfig;
import com.flab.buywithme.domain.Enroll;
import java.sql.Connection;
import java.util.NoSuchElementException;
import java.util.Optional;
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
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestInstance(Lifecycle.PER_CLASS)
@Import({JasyptConfig.class, JpaAuditingConfig.class})
class EnrollRepositoryTest {

    @Autowired
    private EnrollRepository enrollRepository;

    @Autowired
    private DataSource dataSource;

    private final Long memberId = 1L;
    private final Long postId = 1L;
    private final Long enrollId = 1L;

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
    @DisplayName("저장된 enroll 정보 조회 성공")
    void findByPost_IdAndMember_Id() {
        Optional<Enroll> findEnroll = enrollRepository.findByPost_IdAndMember_Id(
                postId, memberId);
        assertEquals(fakeEnroll(enrollId), findEnroll.orElseThrow(NoSuchElementException::new));
    }
}
