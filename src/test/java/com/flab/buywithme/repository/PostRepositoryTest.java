package com.flab.buywithme.repository;

import static com.flab.buywithme.TestFixture.fakeAddress;
import static com.flab.buywithme.TestFixture.fakeMember;
import static com.flab.buywithme.TestFixture.fakePageable;
import static com.flab.buywithme.TestFixture.fakePost;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.flab.buywithme.config.JpaConfig;
import com.flab.buywithme.domain.Post;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(JpaConfig.class)
class PostRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    private final Long addressId = 1L;
    private final Long memberId = 1L;
    private final Long postId = 1L;
    private final Pageable pageable = fakePageable();

    @BeforeEach
    void setUp() {
        addressRepository.save(fakeAddress(addressId));
        memberRepository.save(fakeMember(memberId));
        postRepository.save(fakePost(postId));
        saveNewPostsForTest();
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
        Page<Post> findPosts = postRepository.findByTitleContainingOrContentContaining("",
                pageable);
        assertEquals(2, findPosts.getTotalPages()); //게시글 개수: 3, page size: 2
        assertEquals(LocalDateTime.of(2030, 4, 4, 23, 0, 0),
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

    public void saveNewPostsForTest() {
        Post newPost_1 = fakePost(2L);
        newPost_1.setTitle("검색용"); //기존 title : "test 게시물"
        postRepository.save(newPost_1);
        postRepository.findById(2L)
                .ifPresent(p -> p.setCreatedAt(LocalDateTime.of(2030, 4, 4, 23, 0, 0)));

        Post newPost_2 = fakePost(3L);
        newPost_2.setContent("검색용"); //기존 content : "테스트 목적으로 생성하였음"
        postRepository.save(newPost_2);
        postRepository.findById(3L)
                .ifPresent(p -> p.setCreatedAt(LocalDateTime.of(2018, 4, 4, 23, 0, 0)));
    }
}
