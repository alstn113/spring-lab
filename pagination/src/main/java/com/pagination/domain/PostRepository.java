package com.pagination.domain;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    // JOIN FETCH를 할 경우 메모리에서 페이징을 하게 되므로 성능이슈가 발생할 수 있습니다.
    // 따라서, 페이징을 하기 위해서는 JOIN FETCH를 사용하지 않고, hibernate.default_batch_fetch_size 설정해서 처리한다.

    // 복합 관계에서 JOIN FETCH 후 batch size도 가능하다. 메모리에서 limit 계산은 같다. 실험해보니 더 느리다,,,
    @Query("""
            SELECT p
            FROM Post p
            """)
    Page<Post> findAllWithComments(Pageable pageable);
}
