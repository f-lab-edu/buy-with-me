package com.flab.buywithme.repository;

import com.flab.buywithme.domain.PostComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    List<PostComment> findAllByPost_Id(Long postID);
}
