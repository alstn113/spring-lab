package com.project.event.infra.listener;

import com.project.event.domain.Post;
import com.project.event.domain.PostCreatedEvent;
import com.project.event.domain.PostCreatedFailEvent;
import com.project.event.domain.PostRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PostCreatedEventListener {

    private final PostRepository postRepository;

    public PostCreatedEventListener(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 새로운 트랜잭션을 생성 - 여기서 에러 터진거 롤백해줌.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // 발행 부분이 커밋된 이후에 실행
    public void createAnotherPost(PostCreatedEvent event) {
        String title = event.post().getTitle() + " - Duplicate";
        String imageUrl = event.post().getImageUrl();

        postRepository.save(new Post(title, imageUrl));
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createAnotherPostFail(PostCreatedFailEvent event) {
        String title = event.post().getTitle() + " - Duplicate";
        String imageUrl = event.post().getImageUrl();

        postRepository.save(new Post(title, imageUrl));

        // 리스너 부분에서 에러를 발생시킬 때 리스너 부분은 @Transactional(propagation = Propagation.REQUIRES_NEW)로
        // 새로운 트랜잭션을 생성하고 에러가 발생하면 롤백을 해줍니다. 하지만 이렇게 롤백을 해줘도 이벤트 발행 부분은 롤백이 되지 않습니다.
        throw new IllegalArgumentException("Error occurred in createDuplicatePost");
    }
}
