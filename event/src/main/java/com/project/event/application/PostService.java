package com.project.event.application;

import com.project.event.domain.Post;
import com.project.event.domain.PostCreatedEvent;
import com.project.event.domain.PostCreatedFailEvent;
import com.project.event.domain.PostDeletedEvent;
import com.project.event.domain.PostRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    private final ApplicationEventPublisher publisher;
    private final PostRepository postRepository;

    public PostService(ApplicationEventPublisher publisher, PostRepository postRepository) {
        this.publisher = publisher;
        this.postRepository = postRepository;
    }

    @Transactional
    public Post createPost(Post post) {
        Post savedPost = postRepository.save(post);

        publisher.publishEvent(new PostCreatedEvent(savedPost));

        return savedPost;
    }

    @Transactional
    public Post createPostPublishFail(Post post) {
        Post savedPost = postRepository.save(post);

        publisher.publishEvent(new PostCreatedEvent(savedPost));

        if (true) {
            throw new IllegalArgumentException("Error occurred in createPostPublishFail");
        }

        return savedPost;
    }

    @Transactional
    public Post createPostListenerFail(Post post) {
        Post savedPost = postRepository.save(post);

        publisher.publishEvent(new PostCreatedFailEvent(savedPost));

        return savedPost;
    }

    @Transactional
    public void deletePostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Post not found. id: %d", postId)));
        postRepository.delete(post);

        publisher.publishEvent(new PostDeletedEvent(post));
    }
}
