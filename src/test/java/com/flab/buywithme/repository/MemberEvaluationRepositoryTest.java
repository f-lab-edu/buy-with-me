package com.flab.buywithme.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flab.buywithme.config.JasyptConfig;
import com.flab.buywithme.config.JpaAuditingConfig;
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
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestInstance(Lifecycle.PER_CLASS)
@Import({JasyptConfig.class, JpaAuditingConfig.class})
class MemberEvaluationRepositoryTest {

    @Autowired
    private MemberEvaluationRepository evaluationRepository;

    @Autowired
    private DataSource dataSource;

    private final Long memberId = 1L;

    @BeforeAll
    public void beforeAll() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("sql/data.sql"));
        }
    }

    @AfterAll
    public void afterAll() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("sql/truncate.sql"));
        }
    }

    @Test
    @DisplayName("나에 대한 타인의 매너 평가 결과 가져오기 성공")
    void findAllByColleagueIdSucess() {
        assertTrue(evaluationRepository.findAllByColleagueId(memberId).size() > 0);
    }
}