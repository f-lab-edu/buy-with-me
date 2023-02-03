package com.flab.buywithme.service;

import static com.flab.buywithme.TestFixture.fakeComment;
import static com.flab.buywithme.TestFixture.fakeCommentDTO;
import static com.flab.buywithme.TestFixture.fakeMember;
import static com.flab.buywithme.TestFixture.fakePost;
import static com.flab.buywithme.TestFixture.fakeSubComment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.domain.PostComment;
import com.flab.buywithme.dto.PostCommentDTO;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.PostCommentRepository;
import com.flab.buywithme.service.common.CommonMemberService;
import com.flab.buywithme.service.common.CommonPostService;
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

    @Mock
    private CommonMemberService commonMemberService;

    @Mock
    private CommonPostService commonPostService;

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
    public void saveComment() {
        Member currentMember = fakeMember(memberId);
        Post currentPost = fakePost(postId);
        PostCommentDTO commentDTO = fakeCommentDTO();

        given(commonMemberService.getMember(anyLong()))
                .willReturn(currentMember);
        given(commonPostService.getPost(anyLong()))
                .willReturn(currentPost);
        given(commentRepository.save(any(PostComment.class)))
                .willReturn(comment);

        commentService.saveComment(commentDTO, postId, memberId);

        PostComment expected = PostComment.builder()
                .post(currentPost)
                .member(currentMember)
                .content(commentDTO.getContent())
                .build();

        then(commonMemberService).should().getMember(memberId);
        then(commonPostService).should().getPost(postId);
        then(commentRepository).should().save(expected);
    }

    @Test
    @DisplayName("대댓글 저장 성공")
    public void saveSubComment() {
        Long subCommentId = 2L;
        Member currentMember = fakeMember(memberId);
        Post currentPost = fakePost(postId);
        PostCommentDTO commentDTO = fakeCommentDTO();
        PostComment subComment = fakeSubComment(subCommentId);

        given(commonMemberService.getMember(anyLong()))
                .willReturn(currentMember);
        given(commonPostService.getPost(anyLong()))
                .willReturn(currentPost);
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(comment));
        given(commentRepository.save(any(PostComment.class)))
                .willReturn(subComment);

        commentService.saveSubComment(commentId, commentDTO, postId, memberId);

        PostComment expected = PostComment.builder()
                .post(currentPost)
                .member(currentMember)
                .content(commentDTO.getContent())
                .parent(comment)
                .build();

        then(commonMemberService).should().getMember(memberId);
        then(commonPostService).should().getPost(postId);
        then(commentRepository).should().findById(commentId);
        then(commentRepository).should().save(expected);
    }

    @Test
    @DisplayName("게시글별 댓글 가져오기 성공")
    public void getAllComment() {
        List<PostComment> comments = Arrays.asList(fakeComment(1L), fakeComment(2L));
        given(commentRepository.findAllByPost_Id(anyLong()))
                .willReturn(comments);

        List<PostComment> expectList = Arrays.asList(fakeComment(1L), fakeComment(2L));
        assertEquals(expectList, commentService.getAllComment(postId));
        then(commentRepository).should().findAllByPost_Id(postId);
    }

    @Test
    @DisplayName("특정 댓글 가져오기 성공")
    public void getCommentSuccess() {
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(comment));

        PostComment findComment = commentService.getComment(commentId);

        then(commentRepository).should().findById(commentId);
        assertEquals(findComment, fakeComment(commentId));
    }

    @Test
    @DisplayName("댓글 가져오기 실패")
    public void getCommentFail() {
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> commentService.getComment(commentId));

        then(commentRepository).should().findById(commentId);
        assertEquals(ex.getErrorCode(), ErrorCode.COMMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("댓글 수정 성공")
    public void updateCommentSuccess() {
        PostCommentDTO updateCommentDTO = new PostCommentDTO("수정된 comment");

        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(comment));

        commentService.updateComment(commentId, memberId, updateCommentDTO);

        then(commentRepository).should().findById(commentId);
        assertEquals(comment.getContent(), "수정된 comment");
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    public void deleteCommentSuccess() {
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(comment));

        commentService.deleteComment(commentId, memberId);

        then(commentRepository).should().findById(commentId);
        then(commentRepository).should().delete(fakeComment(commentId));
    }

    @Test
    @DisplayName("작성자가 아니면 댓글 삭제 실패")
    public void deleteCommentFail() {
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(comment));

        CustomException ex = assertThrows(CustomException.class,
                () -> commentService.deleteComment(commentId, 99L));

        then(commentRepository).should().findById(commentId);
        assertEquals(ex.getErrorCode(), ErrorCode.IS_NOT_OWNER);
    }
}
