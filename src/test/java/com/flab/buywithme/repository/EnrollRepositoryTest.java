package com.flab.buywithme.repository;

import static com.flab.buywithme.TestFixture.fakeAddress;
import static com.flab.buywithme.TestFixture.fakeEnroll;
import static com.flab.buywithme.TestFixture.fakeMember;
import static com.flab.buywithme.TestFixture.fakePost;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.flab.buywithme.domain.Enroll;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class EnrollRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EnrollRepository enrollRepository;

    private final Long addressId = 1L;
    private final Long memberId = 1L;
    private final Long postId = 1L;
    private final Long enrollId = 1L;

    @BeforeEach
    void setUp() {
        addressRepository.save(fakeAddress(addressId));
        memberRepository.save(fakeMember(memberId));
        postRepository.save(fakePost(postId));
        enrollRepository.save(fakeEnroll(enrollId));
    }

    @Test
    @DisplayName("저장된 enroll 정보 조회 성공")
    void findByPost_IdAndMember_Id() {
        Optional<Enroll> findEnroll = enrollRepository.findByPost_IdAndMember_Id(
                postId, memberId);
        assertEquals(fakeEnroll(enrollId), findEnroll.orElseThrow(NoSuchElementException::new));
    }
}