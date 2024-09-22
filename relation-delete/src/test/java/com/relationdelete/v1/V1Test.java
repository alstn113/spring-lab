package com.relationdelete.v1;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class V1Test {

    @Autowired
    private PostV1Repository postRepository;

    @Autowired
    private CommentV1Repository commentRepository;

    @Test
    @DisplayName("Post를 저장하면 Comment도 함께 저장된다.")
    void test1() {
        // given
        PostV1 post = new PostV1();
        CommentV1 comment1 = new CommentV1();
        CommentV1 comment2 = new CommentV1();
        post.addComment(comment1);
        post.addComment(comment2);

        // when
        postRepository.save(post);

        // then
        assertThat(postRepository.findAll()).hasSize(1);
        assertThat(commentRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Post를 삭제하면 Comment도 함께 삭제된다.")
    void test2() {
        // given
        PostV1 post = new PostV1();
        CommentV1 comment1 = new CommentV1();
        CommentV1 comment2 = new CommentV1();
        post.addComment(comment1);
        post.addComment(comment2);
        postRepository.save(post);

        // when
        postRepository.delete(post);

        // then
        assertThat(postRepository.findAll()).isEmpty();
        assertThat(commentRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("orphanRemoval = true로 인해서 Post에서 Comment의 관계를 끊으면 자식은 고아 객체로 인식되어 삭제된다.")
    void test3() {
        // given
        PostV1 post = new PostV1();
        CommentV1 comment1 = new CommentV1();
        CommentV1 comment2 = new CommentV1();
        post.addComment(comment1);
        post.addComment(comment2);
        postRepository.save(post);

        // when
        post.getComments().removeFirst();
        postRepository.save(post);

        // then
        assertThat(postRepository.findAll()).hasSize(1);
        assertThat(commentRepository.findAll()).hasSize(1);
    }
}
