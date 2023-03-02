package com.flab.buywithme.controller;

import static com.flab.buywithme.TestFixture.fakePageable;
import static com.flab.buywithme.TestFixture.fakePostDTO;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.buywithme.config.TestConfig;
import com.flab.buywithme.dto.PostDTO;
import com.flab.buywithme.service.PostService;
import com.flab.buywithme.service.common.CommonMemberService;
import com.flab.buywithme.service.common.CommonPostService;
import com.flab.buywithme.utils.SessionConst;
import java.time.LocalDateTime;
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
    private CommonMemberService commonMemberService;

    @MockBean
    private CommonPostService commonPostService;

    private MockHttpSession mockSession;
    private Long postId;
    private Long memberId;
    private Long addressId;

    @BeforeEach
    public void setUp() {
        mockSession = new MockHttpSession();
        mockSession.setAttribute(SessionConst.LOGIN_MEMBER, 1L);
        postId = 1L;
        memberId = 1L;
        addressId = 1L;
    }

    @Test
    @DisplayName("게시글 작성 요청 성공")
    public void createPost() throws Exception {
        PostDTO postDTO = fakePostDTO();

        mockMvc.perform(post("/posts")
                        .content(objectMapper.writeValueAsString(postDTO))
                        .sessionAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postService).should().savePost(postDTO, memberId);
    }

    @Test
    @DisplayName("로그인하지 않은 멤버도 게시글 목록 가져오기 요청 성공")
    public void getAllPosts() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("size", "2");
        Pageable pageable = fakePageable();

        mockMvc.perform(get("/posts")
                        .params(requestParams))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postService).should().getAllPosts(pageable);
    }

    @Test
    @DisplayName("로그인하지 않은 멤버도 게시글 검색 요청 성공")
    public void getPostsWithKeyword() throws Exception {
        String keyword = "test";
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("size", "2");
        requestParams.add("keyword", keyword);
        Pageable pageable = fakePageable();

        mockMvc.perform(get("/posts")
                        .params(requestParams))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postService).should().searchPostWithKeyword(keyword, pageable);
    }

    @Test
    @DisplayName("같은 주소(시-구-동)의 작성자가 쓴 게시글 목록 가져오기 요청 성공")
    public void getSameAddressPosts() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("size", "2");
        Pageable pageable = fakePageable();

        mockMvc.perform(get("/posts/addresses/" + addressId)
                        .params(requestParams)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postService).should().getPostsByAddress(addressId, pageable);
    }

    @Test
    @DisplayName("특정 게시글 가져오기 요청 성공")
    public void getPost() throws Exception {
        mockMvc.perform(get("/posts/" + postId)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(commonPostService).should().getPost(postId);
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

        mockMvc.perform(put("/posts/" + postId)
                        .content(objectMapper.writeValueAsString(updatePostDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("memberId", memberId)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postService).should().updatePost(postId, memberId, updatePostDTO);
    }

    @Test
    @DisplayName("게시글 삭제 요청 성공")
    public void deletePost() throws Exception {
        mockMvc.perform(delete("/posts/" + postId)
                        .sessionAttr("memberId", memberId)
                        .session(mockSession))
                .andExpect(status().is2xxSuccessful())
                .andDo(log());

        then(postService).should().deletePost(postId, memberId);
    }
}
