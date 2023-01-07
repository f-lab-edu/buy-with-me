package com.flab.buywithme.repository;

import com.flab.buywithme.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
