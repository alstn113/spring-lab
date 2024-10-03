package com.pagination;


import java.util.Random;
import com.pagination.domain.comment.Comment;
import com.pagination.domain.comment.CommentRepository;
import com.pagination.domain.log.Log;
import com.pagination.domain.log.LogRepository;
import com.pagination.domain.post.Post;
import com.pagination.domain.post.PostRepository;
import com.pagination.domain.reaction.Reaction;
import com.pagination.domain.reaction.ReactionRepository;
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
    private final ReactionRepository reactionRepository;
    private final LogRepository logRepository;

    @Override
    public void run(ApplicationArguments args) {
        initData();
    }

    public void initData() {
        int postCount = 100;
        for (int i = 0; i < postCount; i++) {
            Post post = postRepository.save(new Post("title %d".formatted(i)));

            // 0 ~ 10개 사이 랜덤한 개수의 댓글을 생성
            int commentCount = new Random().nextInt(11);
            for (int j = 0; j < commentCount; j++) {
                Comment comment = commentRepository.save(new Comment("comment %d".formatted(j), post));

                // 0 ~ 10개 사이 랜덤한 개수의 리액션을 생성
                int reactionCount = new Random().nextInt(11);
                for (int k = 0; k < reactionCount; k++) {
                    Reaction reaction = reactionRepository.save(new Reaction("action %d".formatted(k), comment));

                    // 0 ~ 10개 사이 랜덤한 개수의 로그를 생성
                    int logCount = new Random().nextInt(11);
                    for (int l = 0; l < logCount; l++) {
                        logRepository.save(new Log("content %d".formatted(l), reaction));
                    }
                }
            }
        }
    }
}
