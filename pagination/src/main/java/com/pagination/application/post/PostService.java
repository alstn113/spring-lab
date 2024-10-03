package com.pagination.application.post;

import java.util.List;
import com.pagination.domain.post.Post;
import com.pagination.domain.post.PostRepository;
import com.pagination.domain.post.PostRepositoryCustom;
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
    private final PostRepositoryCustom postRepositoryCustom;

    @Transactional(readOnly = true)
    public List<PostResponse> getPagingPosts(int size, int page) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Post> pagingPosts = postRepository.findAllWithComments(pageRequest);

        return pagingPosts.getContent().stream()
                .map(PostMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(String title) {
        Post post = postRepositoryCustom.searchByWhere(title)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return PostMapper.toResponse(post);
    }
}
