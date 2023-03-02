package com.flab.buywithme.controller;

import static com.flab.buywithme.TestFixture.fakeMemberEvaluationDTO;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.buywithme.config.TestConfig;
import com.flab.buywithme.dto.MemberEvaluationDTO;
import com.flab.buywithme.service.MemberEvaluationService;
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

@WebMvcTest(controllers = MemberEvaluationController.class)
@Import(TestConfig.class)
class MemberEvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberEvaluationService memberEvaluationService;

    private MockHttpSession mockSession;
    private Long postId;
    private Long memberId;
    private Long colleagueId;
    private Long evaluationId;

    @BeforeEach
    public void setUp() {
        mockSession = new MockHttpSession();
        mockSession.setAttribute(SessionConst.LOGIN_MEMBER, 1L);
        postId = 1L;
        memberId = 1L;
        colleagueId = 2L;
        evaluationId = 1L;
    }

    @Test
    @DisplayName("매너 평가 작성 요청 성공")
    public void createEvaluation() throws Exception {
        MemberEvaluationDTO memberEvaluationDTO = fakeMemberEvaluationDTO();

        mockMvc.perform(post("/posts/" + postId + "/members/" + colleagueId + "/evaluations")
                        .content(objectMapper.writeValueAsString(memberEvaluationDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("memberId", memberId)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(memberEvaluationService).should()
                .saveEvaluation(memberEvaluationDTO, postId, memberId, colleagueId);
    }

    @Test
    @DisplayName("멤버별 받은 매너 평가 가져오기 요청 성공")
    public void getAllEvaluations() throws Exception {
        mockMvc.perform(get("/evaluations")
                        .sessionAttr("memberId", memberId)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(memberEvaluationService).should().getAllEvaluations(memberId);
    }

    @Test
    @DisplayName("특정 매너평가 가져오기 요청 성공")
    public void getEvaluation() throws Exception {
        mockMvc.perform(get("/evaluations/" + evaluationId)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(memberEvaluationService).should().getEvaluation(evaluationId);
    }
}
