package com.flab.buywithme.repository;

import static com.flab.buywithme.TestFixture.fakeSubComment;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flab.buywithme.config.JpaConfig;
import com.flab.buywithme.domain.PostComment;
import java.sql.Connection;
import java.util.NoSuchElementException;
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
@Import(JpaConfig.class)
class PostCommentRepositoryTest {

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private DataSource dataSource;

    private final Long commentId = 1L;
    private final Long subCommentId = 2L;

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
    @DisplayName("대댓글 리스트 저장 성공")
    void childrenListCheck() {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(NoSuchElementException::new);
        assertTrue(comment.getChildren().contains(fakeSubComment(subCommentId)));
    }
}