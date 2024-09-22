package com.relationdelete.v3;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class V3Test {

    @Autowired
    private EntityManager em;

    @Autowired
    private PostV3Repository postRepository;

    @Autowired
    private CommentV3Repository commentRepository;

    @Test
    @DisplayName("""
            deleteAll은 내부적으로 delete를 호출하고, delete는 해당하는 엔티티를 찾고 삭제한다.
            그러므로 delete query가 여러 번 나간다.
            """)
    void test1() {
        // given
        PostV3 post = new PostV3();
        CommentV3 comment1 = new CommentV3();
        CommentV3 comment2 = new CommentV3();
        post.addComment(comment1);
        post.addComment(comment2);
        postRepository.save(post);

        // CASCADE.PERSIST 때문에 post에 comment1, comment2가 있다.
        // 그래서 comments를 삭제해도 영속성 컨텍스트에 있는 post의 comments는 삭제되지 않는다.
        em.flush();
        em.clear();

        // when
        // deleteAll은 내부적으로 delete를 호출하고, delete는 해당하는 엔티티를 찾고 삭제한다.
        commentRepository.deleteAll(post.getComments());

        em.flush();
        em.clear();

        // then
        assertThat(postRepository.findAll()).hasSize(1);
        // findAll은 전체를 조회하기 때문에 영속성 컨텍스트가 아닌 DB에서 조회한다. 그러므로 동기화를 위해 flush가 발생한다.
        assertThat(commentRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("""
            deleteAllInBatch는 내부적으로 delete query를 한 번만 날린다.
            그리고 deleteAll과 다르게 조회를 하지 않는다.
            """)
    void test2() {
        // given
        PostV3 post = new PostV3();
        CommentV3 comment1 = new CommentV3();
        CommentV3 comment2 = new CommentV3();
        post.addComment(comment1);
        post.addComment(comment2);
        postRepository.save(post);

        // 위 테스트와 동일한 이유
        em.flush();
        em.clear();

        // when
        commentRepository.deleteAllInBatch(post.getComments());

        em.flush();
        em.clear();

        // then
        assertThat(postRepository.findAll()).hasSize(1);
        assertThat(commentRepository.findAll()).isEmpty();
    }
}
