package com.project.findbyxxxid;

import com.project.findbyxxxid.domain.Comment;
import com.project.findbyxxxid.domain.CommentRepository;
import com.project.findbyxxxid.domain.Post;
import com.project.findbyxxxid.domain.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class QueryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("""
            Post로 Comment들을 조회한다.
            findByPost를 할 경우 where post_id = ? 로 조회한다.
            findByPostId를 할 경우 post를 left join하고 where post.id = ? 로 조회한다.
            """)
    void findByPost() {
        Post post = postRepository.save(new Post());
        commentRepository.save(new Comment(post));
        commentRepository.save(new Comment(post));

        System.out.println("[findByPost]");
        commentRepository.findByPost(post);

        System.out.println("[findByPostId]");
        commentRepository.findByPostId(post.getId());
    }
}
