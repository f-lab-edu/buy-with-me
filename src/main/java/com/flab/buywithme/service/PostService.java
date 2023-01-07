package com.flab.buywithme.service;

import com.flab.buywithme.domain.Post;
import com.flab.buywithme.dto.PostDTO;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.PostRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Long savePost(Post post) {
        return postRepository.save(post).getId();
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPost() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Post> getPost(Long postId) {
        return postRepository.findById(postId);
    }

    public void updatePost(Long postId, PostDTO postDTO) {
        Post post = getPost(postId).orElseThrow(() ->
                new CustomException(ErrorCode.INVALID_UPDATE));

        post.update(postDTO.getTitle(), postDTO.getContent(), postDTO.getTargetNo(),
                postDTO.getExpiration());
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
}
