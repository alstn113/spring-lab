package com.onetomany;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import com.onetomany.domain.Post;
import com.onetomany.domain.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class OneToManyTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("""
            @OneToMany 관계에서 Fetch Join를 사용할 경우 카테시안 곱으로 인해 중복된 데이터가 발생할 수 있다.
            이를 해결하기 위해서는 distinct 키워드를 사용한다. JPQL에서 모든 컬럼을 기준으로 중복을 제거한다.
            즉, select 절에 나열된 모든 필드의 조합이 동일한 경우 중복으로 간주한다.
            주의!!! 스프링 부트 3.0부터 Hibernate 6.1이 사용되고, Hibernate 6.0부터 distinct 키워드가 자동으로 적용된다.
            """)
    void oneToManyDistinctTest() {
        int postRowCount = 12;
        List<Post> posts = postRepository.findPostsWithComments();

        // 참고
        for (Post post : posts) {
            System.out.println(post.getComments());
        }

        assertThat(posts).hasSize(postRowCount);
    }

    @Test
    @DisplayName("""
            Pagination에서 Fetch Join을 사용할 경우 카테시안 곱으로 인해 중복된 데이터가 발생할 수 있다.
            이를 해결하기 위해 distinct 키워드를 사용한다. 위에 있는 테스트와 마찬가지로 자동으로 적용된다.
            limit(h2 first)와 같은 부분이 보이지 않고, 경고 로그가 보인다.
            쿼리에서 컬렉션을 패치(Fetch)하는 경우 모든 엔티티를 로드한 후 페이징을 적용한다. 이 부분을 메모리에서 가져와서 처리한다는 경고이다.
            1:N 관계에서 Fetch Join과 Pagination API을 동시에 사용하면 OutOfMemoryError가 발생할 수 있다.
            다음 테스트를 통해서 해결해보자.
            """)
    void paginationWithFetchJoin() {
        List<Post> posts = postRepository.findWithPaginationJoinFetchO(PageRequest.of(0, 10));

        for (Post post : posts) {
            System.out.println(post.getComments());
        }

        assertThat(posts).hasSize(10);
    }

    @Test
    @DisplayName("""
            Fetch Join 대신 default batch fetch size를 적용하면 해결할 수 있다. yml에 설정을 추가하거나 @BatchSize를 사용하면 된다.
            이것들을 사용하면 where xxx in (?, ?, ? ...)을 통해 batch fetch size만큼 데이터를 나눠서 가져온다.
            """)
    void paginationWithoutFetchJoinAndWithBatchSize() {
        List<Post> posts = postRepository.findWithPaginationJoinFetchX(PageRequest.of(1, 10));

        for (Post post : posts) {
            System.out.println(post.getComments());
        }

        assertThat(posts).hasSize(2);
    }
}
