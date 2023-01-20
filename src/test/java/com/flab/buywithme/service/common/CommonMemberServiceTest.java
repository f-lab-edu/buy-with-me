package com.flab.buywithme.service.common;

import static com.flab.buywithme.TestFixture.fakeMember;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.flab.buywithme.domain.Member;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class CommonMemberServiceTest {

    @InjectMocks
    private CommonMemberService commonMemberService;

    @Mock
    private MemberRepository memberRepository;

    private Long memberId;
    private Member member;

    @BeforeEach
    public void setup() {
        memberId = 1L;
        member = fakeMember(memberId);
    }

    @Test
    @DisplayName("멤버 찾기 성공")
    public void getMemberSuccess() {
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(member));

        Member findMember = commonMemberService.getMember(memberId);

        assertEquals(findMember, fakeMember(memberId));
    }

    @Test
    @DisplayName("멤버 찾기 실패")
    public void getMemberFail() {
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> commonMemberService.getMember(memberId));

        assertEquals(ex.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND);
    }
}
