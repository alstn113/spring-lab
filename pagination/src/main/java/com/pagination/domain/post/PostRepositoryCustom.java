package com.pagination.domain.post;

import java.util.Optional;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Optional<Post> searchByWhere(String title) {
        BooleanExpression where = QPost.post.title.eq(title);

        return queryFactory.selectFrom(QPost.post)
                .where(where)
                .stream().findFirst();
    }
}
