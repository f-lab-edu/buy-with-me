package com.flab.buywithme.service.common;

import com.flab.buywithme.domain.Post;
import com.flab.buywithme.domain.enums.PostStatus;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommonPostService {

    private final PostRepository postRepository;

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    public Post getPostForUpdate(Long postId) {
        return postRepository.findByIdForUpdate(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    @Transactional
    public void increaseJoinCount(Post post) {
        if (post.getStatus() == PostStatus.COMPLETE) {
            throw new CustomException(ErrorCode.GATHERING_FINISHED);
        } else {
            post.increaseCurrentNo();
        }
    }
}
