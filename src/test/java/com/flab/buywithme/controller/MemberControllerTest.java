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
    @DisplayName("?????? ?????? ??????")
    public void signUpSuccess() throws Exception {
        MemberSignUpDTO signUpRequest = MemberSignUpDTO.builder()
                .name("kim")
                .phoneNo("010-1111-1111")
                .loginId("test")
                .password("test1")
                .depth1("?????????")
                .depth2("?????????")
                .depth3("?????????")
                .build();

        mockMvc.perform(post("/members")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());
    }

    @Test
    @DisplayName("????????? ?????? ??? ?????? ?????? ??????")
    public void signUpValidationFail() throws Exception {
        //phoneNo ??????
        MemberSignUpDTO invalidSignUpRequest = MemberSignUpDTO.builder()
                .name("kim")
                .loginId("test")
                .password("test1")
                .depth1("?????????")
                .depth2("?????????")
                .depth3("?????????")
                .build();

        mockMvc.perform(post("/members")
                        .content(objectMapper.writeValueAsString(invalidSignUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("[phoneNo](???)??? ?????????????????? ????????? ???: [null]\n"))
                .andDo(log());
    }

    @Test
    @DisplayName("????????? ??????")
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
    @DisplayName("????????? ??????")
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
                        "{\"error\":\"BAD_REQUEST\",\"message\":\"????????? ????????? ?????? ?????????????????????!\"}"))
                .andDo(log());

        assertNull(session.getAttribute(SessionConst.LOGIN_MEMBER));
    }

    @Test
    @DisplayName("???????????? ??????")
    public void signOutSuccess() throws Exception {
        session.setAttribute(SessionConst.LOGIN_MEMBER, 1L);

        mockMvc.perform(post("/members/signout")
                        .session(session))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        assertTrue(session.isInvalid());
    }

    @Test
    @DisplayName("???????????? ????????? ????????? ???????????? ?????? ??? ????????? ???????????? redirection")
    public void signOutFail() throws Exception {
        mockMvc.perform(post("/members/signout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/members/signin?redirectURL=/**"))
                .andDo(log());
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ??????")
    public void checkPasswordHashing() {
        Member existingMember = fakeMember(1L);
        String original_password = "test1";

        assertNotEquals(original_password, existingMember.getPassword());
        assertEquals(HashingUtil.encrypt(original_password), existingMember.getPassword());
    }

    private Member fakeMember(Long memberId) {
        Member member = new Member(new Address("?????????", "?????????", "?????????"), "kim", "010-1111-1111",
                "test", HashingUtil.encrypt("test1"));
        member.setId(memberId);
        return member;
    }
}
