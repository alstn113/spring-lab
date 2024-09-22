package com.relationdelete.v4;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentV4Repository extends JpaRepository<CommentV4, Long> {

    @Query("delete from CommentV4 c where c.post.id = :postId")
    @Modifying(clearAutomatically = true)
    void deleteAllByPostId(Long postId);
}
