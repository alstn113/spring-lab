package com.relationdelete.v2;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class V2Test {

    @Autowired
    private PostV2Repository postRepository;

    @Test
    @DisplayName("""
            @OnDelete(action = OnDeleteAction.CASCADE)로 인해서 Post가 삭제되면 Comment도 삭제된다.
            CommentV2의 PostV2에 대한 FK에 ON DELETE CASCADE가 설정되어 PostV2가 삭제되면 CommentV2도 삭제된다.
            """)
    void test1() {
        // given
        PostV2 post = new PostV2();
        CommentV2 comment1 = new CommentV2();
        CommentV2 comment2 = new CommentV2();
        post.addComment(comment1);
        post.addComment(comment2);
        postRepository.save(post);

        // when
        postRepository.delete(post);

        // then
        assertThat(postRepository.findAll()).isEmpty();
    }
}
