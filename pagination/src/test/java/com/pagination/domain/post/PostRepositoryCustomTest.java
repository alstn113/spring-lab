package com.pagination.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostRepositoryCustomTest {

    @Autowired
    private PostRepositoryCustom postRepositoryCustom;

    @Autowired
    private PostRepository postRepository;

    @Test
    void searchByWhere() {
        // given
        postRepository.save(new Post("title 1"));
        postRepository.save(new Post("title 2"));
        postRepository.save(new Post("title 3"));
        postRepository.save(new Post("title 4"));
        postRepository.save(new Post("title 5"));

        // when
        Optional<Post> post = postRepositoryCustom.searchByWhere("title 3");

        // then
        assertThat(post).isPresent();
        assertThat(post.get().getTitle()).isEqualTo("title 3");
    }
}
