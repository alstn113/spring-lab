package com.onetomany.domain;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
            SELECT p
            FROM Post p
            JOIN FETCH p.comments
            """)
    List<Post> findPostsWithComments();

    @Query("""
            SELECT p
            FROM Post p
            JOIN FETCH p.comments
            """)
    List<Post> findWithPaginationJoinFetchO(Pageable pageable);

    @Query("""
            SELECT p
            FROM Post p
            """)
    List<Post> findWithPaginationJoinFetchX(Pageable pageable);
}
