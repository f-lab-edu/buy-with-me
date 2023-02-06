package com.flab.buywithme.controller;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flab.buywithme.config.TestConfig;
import com.flab.buywithme.service.NotificationService;
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

@WebMvcTest(controllers = NotificationController.class)
@Import(TestConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    private MockHttpSession mockSession;
    private Long notificationId;
    private Long memberId;

    @BeforeEach
    public void setUp() {
        mockSession = new MockHttpSession();
        mockSession.setAttribute(SessionConst.LOGIN_MEMBER, 1L);
        notificationId = 1L;
        memberId = 1L;
    }

    @Test
    @DisplayName("알림 리스트 가져오기 요청 성공")
    void getAllNotifications() throws Exception {
        mockMvc.perform(get("/notifications")
                        .sessionAttr("memberId", memberId)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(notificationService).should().getAllNotifications(memberId);
    }

    @Test
    @DisplayName("알림 읽기 요청 성공")
    void readNotification() throws Exception {
        mockMvc.perform(put("/notifications/" + notificationId)
                        .sessionAttr("memberId", memberId)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(notificationService).should().readNotification(notificationId, memberId);
    }

    @Test
    @DisplayName("알림 삭제 요청 성공")
    void deleteNotification() throws Exception {
        mockMvc.perform(delete("/notifications/" + notificationId)
                        .sessionAttr("memberId", memberId)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(notificationService).should().deleteNotification(notificationId, memberId);
    }
}