package com.flab.buywithme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.buywithme.config.TestConfig;
import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.domain.PostComment;
import com.flab.buywithme.dto.PostCommentDTO;
import com.flab.buywithme.service.MemberService;
import com.flab.buywithme.service.PostCommentService;
import com.flab.buywithme.service.PostService;
import com.flab.buywithme.utils.HashingUtil;
import com.flab.buywithme.utils.SessionConst;
import java.time.LocalDateTime;
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
        Member currentMember = fakeMember(memberId);
        Post currentPost = fakePost(postId);
        given(memberService.getMember(any(Long.class)))
                .willReturn(currentMember);
        given(postService.getPost(any(Long.class)))
                .willReturn(currentPost);

        PostCommentDTO commentDTO = new PostCommentDTO("test comment");

        mockMvc.perform(post("/posts/" + postId + "/comments")
                        .content(objectMapper.writeValueAsString(commentDTO))
                        .sessionAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        PostComment expected = PostComment.builder()
                .post(currentPost)
                .member(currentMember)
                .content(commentDTO.getContent())
                .build();

        then(commentService).should().saveComment(expected);
    }

    @Test
    @DisplayName("게시글별 댓글 가져오기 요청 성공")
    public void getAllComment() throws Exception {
        mockMvc.perform(get("/posts/" + postId + "/comments")
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(commentService).should().getAllComment(postId);
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

    private Member fakeMember(Long memberId) {
        Member member = new Member(new Address("성남시", "분당구", "판교동"), "kim", "010-1111-1111",
                "test", HashingUtil.encrypt("test1"));
        member.setId(memberId);
        member.getAddress().setId(1L);
        return member;
    }

    private Post fakePost(Long postId) {
        Member member = fakeMember(memberId);
        return Post.builder()
                .id(postId)
                .member(member)
                .address(member.getAddress())
                .title("test 게시물")
                .content("test 목적으로 생성하였음")
                .targetNo(3)
                .expiration(LocalDateTime.of(2023, 4, 4, 23, 0, 0))
                .build();
    }
}