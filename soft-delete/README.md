# Soft Delete(논리 삭제)에 대해서 알아보자

## 참고자료

- [baeldung](https://www.baeldung.com/spring-jpa-soft-delete)
- [사용법 참고](https://lahezy.tistory.com/100)
- [SoftDelete](https://ohksj77.tistory.com/249)

## 대충 개념

물리 삭제는 데이터베이스에서 데이터를 삭제하는 것을 의미한다. 데이터를 삭제하면 데이터베이스에서 완전히 삭제되어 복구할 수 없다.        
논리 삭제는 데이터를 삭제하는 것이 아니라 삭제 플래그를 설정하여 데이터를 삭제한 것처럼 보이게 하는 것을 의미한다.    
논리 삭제는 데이터를 복구할 수 있기 때문에 데이터를 삭제하는 것보다 안전하다.

## 대충 배경 지식

Hibernate 6.4에서부터 @SoftDelete가 나왔지만 true, false만 사용할 수 있음
삭제 시간을 기록하고 싶다면 @SqlDelete와 @SqlRestriction을 사용해야한다.
@Where은 Deprecated 되었으므로 @SqlRestriction을 사용해야한다.

## 사용법

@SQLDelete로 삭제 쿼리를 작성하고 @SQLRestriction으로 삭제되지 않은 엔티티만 조회할 수 있다.

```java

@Entity
@SQLDelete(sql = "UPDATE member SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
public class Member {
    // 생략 ...

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
```

삭제된 엔티티를 조회하려면 @Query를 사용하여 nativeQuery로 작성한다.

```java

@Query(value = "SELECT * FROM member WHERE deleted_at IS NOT NULL", nativeQuery = true)
List<Member> findDeletedMembers();  
```
