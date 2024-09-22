package com.relationdelete.v4;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class V4Test {

    @Autowired
    private PostV4Repository postRepository;

    @Autowired
    private CommentV4Repository commentRepository;

    @Test
    @DisplayName("""
            JPQL를 사용해서 postId로 comment들을 삭제한다.
            """)
    void test1() {
        // given
        PostV4 post = new PostV4();
        CommentV4 comment1 = new CommentV4();
        CommentV4 comment2 = new CommentV4();
        post.addComment(comment1);
        post.addComment(comment2);
        postRepository.save(post);

        // when
        // JPQL는 쿼리문을 실행하기 전에 flush를 자동으로 호출한다.
        // DB에 있는 것을 삭제 후 @Modifying(clearAutomatically = true)로 인해 영속성 컨텍스트를 비워준다.
        commentRepository.deleteAllByPostId(post.getId());

        // then
        assertThat(postRepository.findAll()).hasSize(1);
        assertThat(commentRepository.findAll()).isEmpty();
    }
}
