package com.project.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.event.application.PostService;
import com.project.event.domain.Post;
import com.project.event.domain.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EventTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("""
            일반 이벤트 테스트
            - Post가 삭제되면 PostDeletedEvent 발생
            - ThirdPartyImageStore 리스너에서 PostDeletedEvent를 받아 이미지 삭제 (외부 API 호출)
            """)
    void normalEventTest() {
        Post post = postRepository.save(new Post("title", "https://image.url"));

        postService.deletePostById(post.getId());

        assertThat(postRepository.findById(post.getId())).isEmpty();
    }

    @Test
    @DisplayName("""
            트랜잭션 분리 테스트
            - Post가 생성되면 PostCreatedEvent 발생
            - PostCreatedEventListener 리스너에서 Post를 하나 더 생성
            """)
    void separateTransactionEventTest() throws InterruptedException {
        postService.createPost(new Post("title", "https://image.url"));

        Thread.sleep(500);

        assertThat(postRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("""
            트랜잭션 분리 테스트 (발행 부분 예외)
            - Post가 생성되고 커밋된 후 PostCreatedEvent 발생하는 과정
            - createPostPublishFail 메서드에서 예외 발생 (발행 부분)
            - 발행 부분이 Commit된 후 리스너 부분이 실행되기 때문에 리스너는 실행되지 않는다.
            """)
    void separateTransactionEventPublishFailTest() throws InterruptedException {
        try {
            postService.createPostPublishFail(new Post("title", "https://image.url"));
        } catch (Exception e) {
            // 예외 발생
        }

        Thread.sleep(500);

        assertThat(postRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("""
            트랜잭션 분리 테스트 (리스너 부분 예외)
            - Post가 생성되면 PostCreatedFailEvent 발생
            - PostCreatedEventListener 리스너에서 Post를 하나 더 생성 (예외 발생)
            - 리스너 부분은 롤백되나 발행 부분은 롤백되지 않음
            """)
    void separateTransactionEventListenerFailTest() throws InterruptedException {
        Post post = postService.createPostListenerFail(new Post("title", "https://image.url"));

        Thread.sleep(500);

        assertThat(postRepository.findAll()).hasSize(1)
                .containsExactly(post);
    }
}
