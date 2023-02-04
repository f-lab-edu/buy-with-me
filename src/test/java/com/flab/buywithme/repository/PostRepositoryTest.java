package com.flab.buywithme.repository;

import static com.flab.buywithme.TestFixture.fakePageable;
import static com.flab.buywithme.TestFixture.fakePost;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.flab.buywithme.config.JpaConfig;
import com.flab.buywithme.domain.Post;
import java.sql.Connection;
import java.time.LocalDateTime;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestInstance(Lifecycle.PER_CLASS)
@Import(JpaConfig.class)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private DataSource dataSource;

    private final Long postId = 1L;
    private final Pageable pageable = fakePageable();

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
    @DisplayName("업데이트 목적의 게시글 조회 성공")
    void findByIdForUpdate() {
        Optional<Post> findPost = postRepository.findByIdForUpdate(postId);
        assertEquals(fakePost(postId),
                findPost.orElseThrow(NoSuchElementException::new));
    }

    @Test
    @DisplayName("게시물 페이징 및 최신 생성 일자 순으로 정렬 성공")
    void findByTitleContainingOrContentContaining() {
        Page<Post> findPosts = postRepository.findByTitleContainingOrContentContaining("test 목적",
                pageable);
        assertEquals(2, findPosts.getTotalPages()); //게시글 개수: 3, page size: 2
        assertEquals(LocalDateTime.of(2023, 4, 4, 12, 0, 0),
                findPosts.stream().findFirst().orElseThrow(NoSuchElementException::new)
                        .getCreatedAt());
    }

    @Test
    @DisplayName("키워드 기반 게시글 검색 성공")
    void searchPostWithKeywordSuccess() {
        Page<Post> findPosts = postRepository.findByTitleContainingOrContentContaining("검색용",
                pageable);
        assertEquals(2, findPosts.getTotalElements());
        assertEquals("검색용", findPosts.stream().findFirst().orElseThrow(NoSuchElementException::new)
                .getTitle());
    }

    @Test
    @DisplayName("주소 기반 게시글 검색 성공")
    void searchPSameAddressPostSuccess() {
        Long addressId = 1L;
        Long wrongAddressId = 2L;
        assertEquals(3, postRepository.findByAddress_Id(addressId, pageable).getTotalElements());
        assertEquals(0,
                postRepository.findByAddress_Id(wrongAddressId, pageable).getTotalElements());
    }
}