# OneToMany에서 Fetch Join 사용 시 문제를 알아보자

## 참고 자료

- [OneToMany Distinct Hibernate 6.0](https://delvering.tistory.com/52)
- [OneToMany Fetch Join 데이터 중복과 문제점](https://porolog.tistory.com/59)
- [OneToMany 양방향 편의 메서드](https://skatpdnjs.tistory.com/49)
- [JPA Pagination N+1 Tecoble](https://tecoble.techcourse.co.kr/post/2021-07-26-jpa-pageable/)
- [Spring Batch JPA N+1](https://jojoldu.tistory.com/414)

## 카테시안 곱으로 인한 중복 데이터 문제

OneToMany 관계에서 Fetch Join을 사용하면 데이터 중복이 발생한다. 이는 JPA의 특성 때문인데, JPA는 Fetch Join을 사용하면 카테시안 곱을 사용하여 데이터를 가져오기 때문이다. 이는 데이터
중복을 발생시키는데, 이를 해결하기 위해서는 distinct를 사용하면 된다.
Spring Boot 3.0부터는 Hibernate 6.1을 사용하는데 Hibernate 6.0부터는 distinct를 사용하지 않아도 자동으로
적용해준다. [참고자료](https://github.com/hibernate/hibernate-orm/blob/6.0/migration-guide.adoc#distinct)를 확인해보자.

## 페이지네이션에서 Fetch Join 사용 시 문제점

OneToMany 관계에서 Fetch Join과 Pagination API를 동시에 사용하면 OutOfMemoryError가 발생할 수 있다. 이는 중복된 데이터를 메모리에 가져와서 처리하기 때문이다. 테스트 코드를
참고해보자. 이 문제를 해결하기 위해서 Fetch Join 대신 default batch fetch size를 사용하거나 @BatchSize를 사용하면 된다. 이 경우 한 번 요청하면 배치 사이즈만큼 데이터를
가져온다.
