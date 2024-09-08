package com.cartesian.domain.v1;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostV1Repository extends JpaRepository<PostV1, Long> {

    @Query("SELECT p FROM PostV1 p JOIN FETCH p.comments")
    List<PostV1> findAllPosts();
}
