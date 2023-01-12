package com.flab.buywithme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.domain.PostComment;
import com.flab.buywithme.dto.PostCommentDTO;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.PostCommentRepository;
import com.flab.buywithme.utils.HashingUtil;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostCommentServiceTest {

    @InjectMocks
    private PostCommentService commentService;

    @Mock
    private PostCommentRepository commentRepository;

    private PostComment comment;
    private Long postId;
    private Long memberId;
    private Long commentId;

    @BeforeEach
    public void setup() {
        postId = 1L;
        memberId = 1L;
        commentId = 1L;
        comment = fakeComment(commentId);
    }

    @Test
    @DisplayName("댓글 저장 성공")
    public void savePost() {
        given(commentRepository.save(any(PostComment.class)))
                .willReturn(comment);

        commentService.saveComment(comment);

        then(commentRepository).should().save(fakeComment(commentId));
    }

    @Test
    @DisplayName("게시글별 댓글 가져오기 성공")
    public void getAllComment() {
        List<PostComment> comments = Arrays.asList(fakeComment(1L), fakeComment(2L));
        given(commentRepository.findAllByPost_Id(any(Long.class)))
                .willReturn(comments);

        List<PostComment> expectList = Arrays.asList(fakeComment(1L), fakeComment(2L));
        assertTrue(commentService.getAllComment(postId).equals(expectList));
    }

    @Test
    @DisplayName("특정 댓글 가져오기 성공")
    public void getCommentSuccess() {
        given(commentRepository.findById(any(Long.class)))
                .willReturn(Optional.ofNullable(comment));

        PostComment findComment = commentService.getComment(commentId);

        assertEquals(findComment, fakeComment(commentId));
    }

    @Test
    @DisplayName("댓글 가져오기 실패")
    public void getCommentFail() {
        given(commentRepository.findById(any(Long.class)))
                .willReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> commentService.getComment(commentId));

        assertEquals(ex.getErrorCode(), ErrorCode.COMMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("댓글 수정 성공")
    public void updateCommentSuccess() {
        PostCommentDTO updateCommentDTO = new PostCommentDTO("수정된 comment");

        given(commentRepository.findById(any(Long.class)))
                .willReturn(Optional.ofNullable(comment));

        commentService.updateComment(commentId, memberId, updateCommentDTO);

        assertEquals(comment.getContent(), "수정된 comment");
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    public void deletePostSuccess() {
        given(commentRepository.findById(any(Long.class)))
                .willReturn(Optional.ofNullable(comment));

        commentService.deleteComment(commentId, memberId);

        then(commentRepository).should().delete(fakeComment(commentId));
    }

    @Test
    @DisplayName("작성자가 아니면 댓글 삭제 실패")
    public void deletePostFail() {
        given(commentRepository.findById(any(Long.class)))
                .willReturn(Optional.ofNullable(comment));

        CustomException ex = assertThrows(CustomException.class,
                () -> commentService.deleteComment(commentId, 99L));

        assertEquals(ex.getErrorCode(), ErrorCode.IS_NOT_OWNER);
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

    private PostComment fakeComment(Long commentId) {
        Post post = fakePost(postId);
        Member member = fakeMember(memberId);
        return PostComment.builder()
                .id(commentId)
                .post(post)
                .member(member)
                .content("test comment")
                .build();
    }
}