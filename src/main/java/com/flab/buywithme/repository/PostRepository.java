package com.flab.buywithme.repository;

import com.flab.buywithme.domain.Post;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select p from Post p where p.id = :id")
    Optional<Post> findByIdForUpdate(@Param("id") Long postId);

    @Query(value = "select p from Post p where function('match_against', p.title, p.content, :keyword) > 0")
    Page<Post> findByTitleContainingOrContentContaining(@Param("keyword") String keyword,
            Pageable pageable);

    Page<Post> findByAddress_Id(Long addressId, Pageable pageable);

    @Query(value = "select distinct p from Post p join fetch p.member join fetch p.address join fetch p.comments",
            countQuery = "select count(p) from Post p")
    Page<Post> findAllUsingFetchJoin(Pageable pageable);
}
