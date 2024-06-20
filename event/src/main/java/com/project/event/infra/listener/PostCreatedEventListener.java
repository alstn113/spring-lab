package com.project.event.infra.listener;

import com.project.event.application.PostService;
import com.project.event.domain.Post;
import com.project.event.domain.PostCreatedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PostCreatedEventListener {

    private final PostService postService;

    public PostCreatedEventListener(PostService postService) {
        this.postService = postService;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createDuplicatePost(PostCreatedEvent event) {
        String title = event.post().getTitle() + " - Duplicate";
        String imageUrl = event.post().getImageUrl();

        postService.createPost(new Post(title, imageUrl));

        throw new IllegalArgumentException("Test rollback");
    }
}
