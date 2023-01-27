package com.flab.buywithme.repository;

import static com.flab.buywithme.TestFixture.fakeAddress;
import static com.flab.buywithme.TestFixture.fakeMember;
import static com.flab.buywithme.TestFixture.fakePost;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.flab.buywithme.domain.Post;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
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

    @BeforeEach
    void setUp() {
        addressRepository.save(fakeAddress(addressId));
        memberRepository.save(fakeMember(memberId));
        postRepository.save(fakePost(postId));
    }

    @Test
    void findByIdForUpdate() {
        Optional<Post> findPost = postRepository.findByIdForUpdate(postId);
        assertEquals(fakePost(postId), findPost.orElseThrow(NoSuchElementException::new));
    }
}