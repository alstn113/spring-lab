package com.onetomanydelete;

import static org.assertj.core.api.Assertions.assertThat;

import com.onetomanydelete.domain.Comment;
import com.onetomanydelete.domain.CommentRepository;
import com.onetomanydelete.domain.Post;
import com.onetomanydelete.domain.PostRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class OneToManyDeleteTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("CASCADE REMOVE로 인해서 Post가 삭제되면 Comment도 삭제된다.")
    void test1() {
        Post post = new Post("Post 1");
        Comment comment1 = new Comment("Comment 1", post);
        Comment comment2 = new Comment("Comment 2", post);
        post.addComment(comment1);
        post.addComment(comment2);
        postRepository.save(post);

        assertThat(postRepository.findAll()).hasSize(1);
        assertThat(commentRepository.findAll()).hasSize(2);

        postRepository.delete(post);

        assertThat(postRepository.findAll()).isEmpty();
        assertThat(commentRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName(""" 
            CASCADE REMOVE + orphanRemoval = true로 인해서
            Post에서 Comment의 관계를 끊으면 자식은 고아 객체로 인식되어 삭제된다.
            """)
    void test2() {
        Post post = new Post("Post 1");
        Comment comment1 = new Comment("Comment 1", post);
        Comment comment2 = new Comment("Comment 2", post);
        post.addComment(comment1);
        post.addComment(comment2);
        postRepository.save(post);

        post.getComments().remove(0);

        assertThat(postRepository.findAll()).hasSize(1);
        assertThat(commentRepository.findAll()).hasSize(1);
    }
}
