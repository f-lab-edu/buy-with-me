package com.flab.buywithme.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.buywithme.config.TestConfig;
import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.dto.MemberSignInDTO;
import com.flab.buywithme.dto.MemberSignUpDTO;
import com.flab.buywithme.service.MemberService;
import com.flab.buywithme.utils.HashingUtil;
import com.flab.buywithme.utils.SessionConst;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MemberController.class)
@Import(TestConfig.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
    }

    @Test
    @DisplayName("회원 가입 성공")
    public void signUpSuccess() throws Exception {
        MemberSignUpDTO signUpRequest = MemberSignUpDTO.builder()
                .name("kim")
                .phoneNo("010-1111-1111")
                .loginId("test")
                .password("test1")
                .depth1("성남시")
                .depth2("분당구")
                .depth3("판교동")
                .build();

        mockMvc.perform(post("/members")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());
    }

    @Test
    @DisplayName("필수값 누락 시 회원 가입 실패")
    public void signUpValidationFail() throws Exception {
        //phoneNo 누락
        MemberSignUpDTO invalidSignUpRequest = MemberSignUpDTO.builder()
                .name("kim")
                .loginId("test")
                .password("test1")
                .depth1("성남시")
                .depth2("분당구")
                .depth3("판교동")
                .build();

        mockMvc.perform(post("/members")
                        .content(objectMapper.writeValueAsString(invalidSignUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("phoneNo(은)는 필수 값 입니다\n입력된 값: [null]\n"))
                .andDo(log());
    }

    @Test
    @DisplayName("로그인 성공")
    public void signInSuccess() throws Exception {
        MemberSignInDTO loginRequest = new MemberSignInDTO("test", "test1");
        Member existingMember = fakeMember(1L);

        given(memberService.signIn(any(String.class), any(String.class)))
                .willReturn(existingMember);

        mockMvc.perform(post("/members/signin")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(memberService).should().signIn("test", HashingUtil.encrypt("test1"));
        assertEquals(1L, session.getAttribute(SessionConst.LOGIN_MEMBER));
    }

    @Test
    @DisplayName("로그인 실패")
    public void signInFail() throws Exception {
        MemberSignInDTO invalidLoginRequest = new MemberSignInDTO("test", "wrongPW");

        given(memberService.signIn(any(String.class), any(String.class)))
                .willReturn(null);

        mockMvc.perform(post("/members/signin")
                        .content(objectMapper.writeValueAsString(invalidLoginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json(
                        "{\"error\":\"BAD_REQUEST\",\"message\":\"잘못된 아이디 혹은 비밀번호입니다!\"}"))
                .andDo(log());

        assertNull(session.getAttribute(SessionConst.LOGIN_MEMBER));
    }

    @Test
    @DisplayName("로그아웃 성공")
    public void signOutSuccess() throws Exception {
        session.setAttribute(SessionConst.LOGIN_MEMBER, 1L);

        mockMvc.perform(post("/members/signout")
                        .session(session))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        assertTrue(session.isInvalid());
    }

    @Test
    @DisplayName("로그인한 멤버가 아니면 로그아웃 시도 시 로그인 페이지로 redirection")
    public void signOutFail() throws Exception {
        mockMvc.perform(post("/members/signout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/members/signin?redirectURL=/**"))
                .andDo(log());
    }

    @Test
    @DisplayName("비밀번호 해싱 결과 비교")
    public void checkPasswordHashing() {
        Member existingMember = fakeMember(1L);
        String original_password = "test1";

        assertNotEquals(original_password, existingMember.getPassword());
        assertEquals(HashingUtil.encrypt(original_password), existingMember.getPassword());
    }

    private Member fakeMember(Long memberId) {
        Member member = new Member(new Address("성남시", "분당구", "판교동"), "kim", "010-1111-1111",
                "test", HashingUtil.encrypt("test1"));
        member.setId(memberId);
        return member;
    }
}
