package com.pagination;


import java.util.Random;
import com.pagination.domain.Comment;
import com.pagination.domain.CommentRepository;
import com.pagination.domain.Post;
import com.pagination.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    public void run(ApplicationArguments args) {
        initData();
    }

    public void initData() {
        int postCount = 100;
        for (int i = 0; i < postCount; i++) {
            Post post = postRepository.save(new Post("title %d".formatted(i)));

            // 0 ~ 20개 사이 랜덤한 개수의 댓글을 생성
            int commentCount = new Random().nextInt(21);
            for (int j = 0; j < commentCount; j++) {
                commentRepository.save(new Comment("comment %d".formatted(j), post));
            }
        }
    }
}
