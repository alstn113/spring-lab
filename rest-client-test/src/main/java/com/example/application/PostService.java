package com.example.application;

import com.example.domain.Post;
import com.example.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Transactional
    public PostResponse createPost(String title, String body) {
        Post post = new Post(title, body);
        Post savedPost = postRepository.save(post);

        return postMapper.toResponse(savedPost);
    }
}
