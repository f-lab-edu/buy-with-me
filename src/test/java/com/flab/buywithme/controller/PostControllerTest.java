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
import com.flab.buywithme.dto.PostDTO;
import com.flab.buywithme.service.MemberService;
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

@WebMvcTest(controllers = PostController.class)
@Import(TestConfig.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private MemberService memberService;

    private MockHttpSession mockSession;
    private Long postID;
    private Long memberID;

    @BeforeEach
    public void setUp() {
        mockSession = new MockHttpSession();
        mockSession.setAttribute(SessionConst.LOGIN_MEMBER, 1L);
        postID = 1L;
        memberID = 1L;
    }

    @Test
    @DisplayName("게시글 작성 요청 성공")
    public void createPost() throws Exception {
        Member currentMember = fakeMember(memberID);
        given(memberService.findById(any(Long.class)))
                .willReturn(currentMember);

        PostDTO postDTO = PostDTO.builder()
                .title("test 게시물")
                .content("test 목적으로 생성하였음")
                .targetNo(3)
                .expiration(LocalDateTime.of(2023, 4, 4, 23, 0, 0))
                .build();

        mockMvc.perform(post("/posts")
                        .content(objectMapper.writeValueAsString(postDTO))
                        .sessionAttr("memberId", memberID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());
    }

    @Test
    @DisplayName("로그인하지 않은 멤버도 전체 게시글 가져오기 요청 성공")
    public void getAllPost() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postService).should().getAllPost();
    }

    @Test
    @DisplayName("특정 게시글 가져오기 요청 성공")
    public void getPost() throws Exception {
        mockMvc.perform(get("/posts/" + postID)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postService).should().getPost(postID);
    }

    @Test
    @DisplayName("게시글 업데이트 요청 성공")
    public void updatePost() throws Exception {
        PostDTO updatePostDTO = PostDTO.builder()
                .title("수정 test 게시물")
                .content("test 목적으로 생성하였음")
                .targetNo(3)
                .expiration(LocalDateTime.of(2023, 4, 4, 23, 0, 0))
                .build();

        mockMvc.perform(put("/posts/" + postID)
                        .content(objectMapper.writeValueAsString(updatePostDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("memberId", memberID)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postService).should().updatePost(postID, memberID, updatePostDTO);
    }

    @Test
    @DisplayName("게시글 삭제 요청 성공")
    public void deletePost() throws Exception {
        mockMvc.perform(delete("/posts/" + postID)
                        .sessionAttr("memberId", memberID)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postService).should().deletePost(postID, memberID);
    }

    private Member fakeMember(Long memberId) {
        Member member = new Member(new Address("성남시", "분당구", "판교동"), "kim", "010-1111-1111",
                "test", HashingUtil.encrypt("test1"));
        member.setId(memberId);
        member.getAddress().setId(1L);
        return member;
    }
}