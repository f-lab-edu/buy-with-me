package com.flab.buywithme.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.buywithme.config.TestConfig;
import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.service.MemberService;
import com.flab.buywithme.utils.HashingUtil;
import com.flab.buywithme.utils.SessionConst;
import java.util.HashMap;
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
    void signUpSuccess() throws Exception {
        HashMap<String, String> body = new HashMap<>();
        body.put("name", "kim");
        body.put("phoneNo", "010-1111-1111");
        body.put("loginId", "test");
        body.put("password", "test1");
        body.put("depth1", "성남시");
        body.put("depth2", "분당구");
        body.put("depth3", "판교동");

        mockMvc.perform(post("/members")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());
    }

    @Test
    @DisplayName("회원 가입 validation 실패")
    void signUpValidationFail() throws Exception {
        HashMap<String, String> body = new HashMap<>();
        body.put("name", "kim");
        //phoneNo 존재 x
        body.put("loginId", "test");
        body.put("password", "test1");
        body.put("depth1", "성남시");
        body.put("depth2", "분당구");
        body.put("depth3", "판교동");

        mockMvc.perform(post("/members")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("[phoneNo](은)는 필수값입니다 입력된 값: [null]\n"))
                .andDo(log());
    }

    @Test
    @DisplayName("로그인 성공")
    void signInSuccess() throws Exception {
        HashMap<String, String> body = new HashMap<>();
        body.put("loginId", "test");
        body.put("password", "test1");
        Member existingMember = fakeMember(1L);

        given(memberService.signIn(body.get("loginId"), HashingUtil.encrypt(body.get("password"))))
                .willReturn(existingMember);

        mockMvc.perform(post("/members/signin")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        assertEquals(1L, session.getAttribute(SessionConst.LOGIN_MEMBER));
    }

    @Test
    @DisplayName("로그인 실패")
    void signInFail() throws Exception {
        HashMap<String, String> body = new HashMap<>();
        body.put("loginId", "test");
        body.put("password", "wrongVal");

        given(memberService.signIn(body.get("loginId"), HashingUtil.encrypt(body.get("password"))))
                .willReturn(null);

        mockMvc.perform(post("/members/signin")
                        .content(objectMapper.writeValueAsString(body))
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
    void signOutSuccess() throws Exception {
        session.setAttribute(SessionConst.LOGIN_MEMBER, 1L);

        mockMvc.perform(post("/members/signout")
                        .session(session))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        assertTrue(session.isInvalid());
    }

    @Test
    @DisplayName("로그인한 멤버가 아니면 로그아웃 시도 시 로그인 페이지로 redirection")
    void signOutFail() throws Exception {
        mockMvc.perform(post("/members/signout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/members/signin?redirectURL=/**"))
                .andDo(log());
    }

    @Test
    @DisplayName("비밀번호 해싱 결과 비교")
    void checkPasswordHashing() {
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
