package com.pagination.application;

import java.util.List;
import com.pagination.domain.Post;
import com.pagination.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper PostMapper;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public List<PostResponse> getPagingPosts(int size, int page) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Post> pagingPosts = postRepository.findAllWithComments(pageRequest);

        return pagingPosts.getContent().stream()
                .map(PostMapper::toResponse)
                .toList();
    }
}
