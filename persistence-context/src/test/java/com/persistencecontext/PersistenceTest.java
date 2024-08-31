package com.persistencecontext;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // 테스트 격리
@DisplayName("영속성 컨텍스트 테스트")
class PersistenceTest {

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("엔티티를 저장")
    void save() {
        // save 메서드 내부에서 isNew(id 유무를 판단)인지 확인하고 persist 또는 merge 호출
        // merge 의 대상이면 select 쿼리를 통해 영속성 컨텍스트에 엔티티를 가져온 후 값을 복사하고 merge 수행
        // db 에서 id가 조회되지 않으면 persist 수행
        memberRepository.save(new Member("a", "b"));
        Iterable<Member> members = memberRepository.findAll(); // db -> 영속성 컨텍스트 -> 컬렉션으로 조회
        assertThat(members).hasSize(1);
        assertThat(members.iterator().next().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("persist 메서드를 호출하면 영속성 컨텍스트에 저장되지만 DB에 반영되지 않음")
    void persist() {
        Member member = new Member("first name", "last name"); // 비영속 상태
        assertThat(entityManager.contains(member)).isFalse();

        entityManager.persist(member); // 비영속 상태 -> 영속 상태
        assertThat(entityManager.contains(member)).isTrue();
        // *주의* id가 identity 전략이면 persist 시 id를 받기 위해 insert 하기 때문에 DB에 반영되고, 영속성 컨텍스트에 저장

        List<Member> members = findAllByJdbc();
        assertThat(members).isEmpty(); // 영속 상태이나 아직 DB에 반영되지 않음
    }

    @Test
    @DisplayName("flush 메서드를 호출하면 영속성 컨텍스트의 내용을 DB에 반영")
    void flush() {
        Member member = new Member("first name", "last name");
        entityManager.persist(member);
        entityManager.flush(); // 영속성 컨텍스트의 내용을 DB에 반영
        assertThat(entityManager.contains(member)).isTrue(); // flush 후에도 영속성 상태 유지

        List<Member> members = findAllByJdbc();
        assertThat(members).hasSize(1);
    }

    @Test
    @DisplayName("detach 메서드를 호출하면 영속성 컨텍스트에서 엔티티를 제거")
    void detach() {
        Member member = new Member("first name", "last name");
        entityManager.persist(member);
        assertThat(entityManager.contains(member)).isTrue();

        entityManager.detach(member); // 영속 상태 -> 준영속 상태
        assertThat(entityManager.contains(member)).isFalse(); // 식별자는 유지되지만 영속성 컨텍스트에서 찾을 수 없음
    }

    @Test
    @DisplayName("준영속 상태의 엔티티를 수정하면 DB에 반영되지 않음")
    void detachAgainstPersist() {
        Member member = new Member("first name", "last name");
        entityManager.persist(member); // 비영속 상태 -> 영속 상태
        entityManager.flush();

        entityManager.detach(member); // 영속 상태 -> 준영속 상태
        member.updateName("new first name", "new last name");
        entityManager.flush();

        List<Member> members = findAllByJdbc();

        assertThat(members.get(0).getFirstName()).isEqualTo("first name");
    }

    @Test
    @DisplayName("영속 상태의 엔티티를 수정하면 DB에 반영됨")
    void persistAgainstDetach() {
        Member member = new Member("first name", "last name");
        entityManager.persist(member); // 비영속 상태 -> 영속 상태
        entityManager.flush();

        member.updateName("new first name", "new last name");
        entityManager.flush();

        List<Member> members = findAllByJdbc();

        assertThat(members.get(0).getFirstName()).isEqualTo("new first name");
    }

    @Test
    @DisplayName("merge 메서드를 호출하면 준영속 상태의 엔티티를 다시 영속 상태로 변경")
    void merge() {
        Member member = new Member("first name", "last name");
        entityManager.persist(member); // 비영속 상태 -> 영속 상태
        entityManager.flush();

        entityManager.detach(member); // 영속 상태 -> 준영속 상태
        member.updateName("new first name", "new last name");
        entityManager.merge(member); // 준영속 상태 -> 영속 상태
        entityManager.flush();

        List<Member> members = findAllByJdbc();

        assertThat(members.get(0).getFirstName()).isEqualTo("new first name");
    }

    @Test
    void remove() {
        Member member = new Member("first name", "last name");
        entityManager.persist(member); // 비영속 상태 -> 영속 상태
        entityManager.flush(); // 영속성 컨텍스트의 내용을 DB에 반영

        List<Member> members = findAllByJdbc();
        assertThat(members).hasSize(1);

        entityManager.remove(member); // 영속 상태 -> 삭제 상태 (영속성 컨텍스트에서 제거)
        List<Member> afterRemoveMembers = findAllByJdbc();
        assertThat(afterRemoveMembers).hasSize(1); // DB에 반영되지 않음

        entityManager.flush(); // 영속성 컨텍스트의 내용을 DB에 반영
        List<Member> afterRemoveAndFlushMembers = findAllByJdbc();
        assertThat(afterRemoveAndFlushMembers).isEmpty(); // 삭제된 엔티티가 DB에서도 삭제됨
    }


    // jpa 와 달리 영속성 컨텍스트를 확인하지 않고 db에서 직접 조회한다.
    private List<Member> findAllByJdbc() {
        String sql = "select * from member";
        List<Member> members = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Member(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                ));

        return members;
    }
}
