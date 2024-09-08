package com.onetomany.domain;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class V1Test {

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("페이지네이션 테스트")
    void paginationTestV1() {
        List<Post> posts = postRepository.findWithPaginationV1(PageRequest.of(0, 10));
        for (Post post : posts) {
            System.out.println(post.getComments());
        }
    }

    @Test
    @DisplayName("페이지네이션 테스트")
    void paginationTestV2() {
        List<Post> posts = postRepository.findWithPagination2(PageRequest.of(0, 10));
        for (Post post : posts) {
            System.out.println(post.getComments());
        }
    }
}
