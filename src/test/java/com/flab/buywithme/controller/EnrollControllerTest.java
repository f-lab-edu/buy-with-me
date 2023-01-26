package com.flab.buywithme.controller;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flab.buywithme.config.TestConfig;
import com.flab.buywithme.service.EnrollService;
import com.flab.buywithme.utils.SessionConst;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EnrollController.class)
@Import(TestConfig.class)
class EnrollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnrollService enrollService;

    private MockHttpSession mockSession;
    private Long memberId;
    private Long postId;

    @BeforeEach
    void setUp() {
        mockSession = new MockHttpSession();
        mockSession.setAttribute(SessionConst.LOGIN_MEMBER, 1L);
        postId = 1L;
        memberId = 1L;
    }

    @Test
    @DisplayName("구매 참여 요청 성공")
    void join() throws Exception {
        mockMvc.perform(post("/posts/" + postId + "/enrolls")
                        .sessionAttr("memberId", memberId)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(enrollService).should().joinBuying(postId, memberId);
    }

    @Test
    @DisplayName("구매 참여 취소 요청 성공")
    void cancel() throws Exception {
        mockMvc.perform(delete("/posts/" + postId + "/enrolls")
                        .sessionAttr("memberId", memberId)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(enrollService).should().cancelJoining(postId, memberId);
    }
}