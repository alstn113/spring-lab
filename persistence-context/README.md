# 영속성 컨텍스트에 대해서 알아보자

## 참고

- [우테코 영속성 컨텍스트 실습](https://techcourse.woowahan.com/s/mgadTu2Z/ls/0QL9ismD)
- [우테코 피드백 강의](https://techcourse.woowahan.com/s/mgadTu2Z/ls/0QL9ismD)
- [우테코 테코톡](https://www.youtube.com/watch?v=kJexMyaeHDs&t=1s)
- [블로그 정리](https://kkkdh.tistory.com/entry/JPA-영속성-컨텍스트에-대한-정리)

## 다양한 상태에 대해서 알아보자

1. 비영속 상태 (Transient)
    - 설명: 엔티티가 영속성 컨텍스트에 존재하지 않는 상태입니다. 데이터베이스에 저장되지 않고, JPA에 의해 관리되지 않습니다.
    - 예: new Member()와 같이 생성된 엔티티 객체.
2. 영속 상태 (Managed)
    - 설명: 엔티티가 영속성 컨텍스트에 저장되어 JPA에 의해 관리되는 상태입니다. 변경 사항이 자동으로 감지되고, 데이터베이스에 반영됩니다.
    - 예: entityManager.persist(member)로 영속성 컨텍스트에 저장된 엔티티.
3. 준영속 상태 (Detached)
    - 설명: 영속성 컨텍스트에서 분리된 상태로, 더 이상 JPA에 의해 관리되지 않습니다. 하지만 데이터베이스에는 여전히 저장되어 있습니다.
    - 예: entityManager.detach(member)로 분리된 엔티티 또는 트랜잭션이 종료된 후의 엔티티.
4. 삭제 상태 (Removed)
    - 설명: 영속성 컨텍스트에서 삭제될 예정인 상태입니다. entityManager.remove(member)를 호출하면 이 상태로 전환됩니다.
    - 예: 삭제된 엔티티는 다음 flush() 호출 시 데이터베이스에서 삭제됩니다.

## flush에 대해서 자세히 알아보자

> flush란 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영하는 것을 의미한다.

### flush가 발생하는 상황

1. 변경 내용을 데이터베이스에 동기화하기 위해 em.flush()로 직접 호출한 경우
2. 트랜잭션을 커밋하는 시점에 자동으로 호출한다. (flush -> commit)
3. JPQL 쿼리를 실행하는 시점에 자동으로 호출한다. (flush -> JPQL query)
    - JPQL 쿼리를 사용하면 데이터베이스를 직접 조회힌다.
    - 영속성 컨텍스트의 내용이 데이터베이스에 반영되어야 쿼리 결과가 일관성 있게 나오도록 보장한다.

### 더티 체킹(dirty checking)이란?

1. 영속성 컨텍스트는 엔티티를 영속 상태로 만들 때 스냅샷을 찍어둔다.
2. 트랜잭션이 진행되는 동안 엔티티의 속성이 변경되면, JPA는 현재 상태와 스냅샷을 비교하여 변경 사항을 감지한다.
3. 트랜잭션의 커밋(flush 포함)되거나 flush 가 호출되면, 더티 체킹이 수행된다.
4. 변경 사항을 비교하고, 변경된 필드에 대해서 SQL UPDATE 쿼리를 쓰기 지연 SQL 저장소에 저장한다.
5. flush가 호출되면 쓰기 지연 SQL 저장소에 저장된 쿼리들이 데이터베이스에 적용된다.

### 참고할 점

- flush 는 영속성 컨텍스트를 비우지 않는다.
- 영속성 컨텍스트의 변경 내용을 데이터베이스에 동기화한다.

## 동일성에 대해서 알아보자

동일한 엔티티를 두 번 조회하여 다른 인스턴스를 얻는 경우는 문제가 없다.

```java
public void testEquality(EntityManager em) {
    Member member1 = em.find(Member.class, 1L);
    Member member2 = em.find(Member.class, 1L);

    System.out.println(member1 == member2); // true
    System.out.println(member1.equals(member2)); // true
}
```

하지만 다른 트랜잭션에서 동일한 엔티티를 조회하는 경우는 주의해야 한다.
equals()와 hashCode() 메서드 구현 시 식별자(id) 기준으로 작성해야 한다.

```java
public void testEquality(EntityManager em1, EntityManager em2) {
    Long memberId = 1L;

    Member member1 = em1.find(Member.class, memberId);
    Member member2 = em2.find(Member.class, memberId);

    System.out.println(member1 == member2); // false
    System.out.println(member1.equals(member2)); // true (id 기준으로 equals, hashCode 구현)
}
```

## Cascade(전이)에 대해서 알아보자

Cascade는 연관 관계에 있는 엔티티에 대한 영속성 전이 기능을 제공한다.
속성에는 PERIST, MERGE, REMOVE, REFRESH, DETACH, 모두를 포함하는 ALL이 있다.
아래의 예시에서 Post 엔티티를 영속 상태로 만들 때, 연관된 Comment 엔티티도 함께 영속 상태로 만들어준다.

```java

@Entity
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "post_id")
    private Post post;
}
```
