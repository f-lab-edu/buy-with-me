package com.flab.buywithme.controller;

import static com.flab.buywithme.TestFixture.fakeCommentDTO;
import static com.flab.buywithme.TestFixture.fakePageable;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.buywithme.config.TestConfig;
import com.flab.buywithme.dto.PostCommentDTO;
import com.flab.buywithme.service.MemberService;
import com.flab.buywithme.service.PostCommentService;
import com.flab.buywithme.service.PostService;
import com.flab.buywithme.utils.SessionConst;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

@WebMvcTest(controllers = PostCommentController.class)
@Import(TestConfig.class)
class PostCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostCommentService commentService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private PostService postService;

    private MockHttpSession mockSession;
    private Long postId;
    private Long memberId;
    private Long commentId;

    @BeforeEach
    public void setUp() {
        mockSession = new MockHttpSession();
        mockSession.setAttribute(SessionConst.LOGIN_MEMBER, 1L);
        postId = 1L;
        memberId = 1L;
        commentId = 1L;
    }

    @Test
    @DisplayName("댓글 작성 요청 성공")
    public void createComment() throws Exception {
        PostCommentDTO commentDTO = fakeCommentDTO();

        mockMvc.perform(post("/posts/" + postId + "/comments")
                        .content(objectMapper.writeValueAsString(commentDTO))
                        .sessionAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(commentService).should().saveComment(commentDTO, postId, memberId);
    }

    @Test
    @DisplayName("대댓글 작성 요청 성공")
    public void createSubComment() throws Exception {
        PostCommentDTO commentDTO = fakeCommentDTO();

        mockMvc.perform(post("/posts/" + postId + "/comments/" + commentId)
                        .content(objectMapper.writeValueAsString(commentDTO))
                        .sessionAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(commentService).should().saveSubComment(commentId, commentDTO, postId, memberId);
    }

    @Test
    @DisplayName("게시글별 댓글 가져오기 요청 성공")
    public void getAllComment() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("size", "2");
        Pageable pageable = fakePageable();

        mockMvc.perform(get("/posts/" + postId + "/comments")
                        .session(mockSession)
                        .params(requestParams))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(commentService).should().getAllComment(postId, pageable);
    }

    @Test
    @DisplayName("특정 댓글 가져오기 요청 성공")
    public void getComment() throws Exception {
        mockMvc.perform(get("/posts/" + postId + "/comments/" + commentId)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(commentService).should().getComment(commentId);
    }

    @Test
    @DisplayName("댓글 업데이트 요청 성공")
    public void updateComment() throws Exception {
        PostCommentDTO commentDTO = new PostCommentDTO("수정된 comment");
        mockMvc.perform(put("/posts/" + postId + "/comments/" + commentId)
                        .content(objectMapper.writeValueAsString(commentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("memberId", memberId)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(commentService).should().updateComment(commentId, memberId, commentDTO);
    }

    @Test
    @DisplayName("댓글 삭제 요청 성공")
    public void deletePost() throws Exception {
        mockMvc.perform(delete("/posts/" + postId + "/comments/" + commentId)
                        .sessionAttr("memberId", memberId)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(commentService).should().deleteComment(commentId, memberId);
    }
}
