# findByXxxId의 문제점에 대해서 알아보자

## 참고자료

- [findByXxxId 문제점](https://velog.io/@ohzzi/Data-Jpa-findByXXXId-는-불필요한-join을-유발한다)

## 주의

- ⚠️ 스프링 부트 3.2.6(hibernate-core 6.4.8.Final)까지 findByPostId는 left join 후 where 절로 처리한다.
- ⚠️ 스프링 부트 3.2.7(hibernate-core 6.4.9.Final)부터 findByPostId는 left join 없이 where 절로 처리한다.    

## 대충 개념

Post와 Comment가 1:N 관계일 때, findByPost와 findByPostId의 쿼리가 동일할 것으로 기대된다.     
하지만 findByPostId는 left join 후 where 절로 처리하고, findByPost는 where 절로 처리한다.     
다음의 이미지를 참고하자.    

![image](https://github.com/alstn113/alstn113/assets/75781414/8669bb30-1b1a-47bb-b74e-49c8e7c2dea9)

그 이유는 무엇일까?     

findByXXXId는 XXX_id라는 외래 키를 가지고 조회하는 것이 아닌, XXX의 식별자를 가지고 조회하는 것이다.     
따라서 findByXXXId는 left join 후 where 절로 처리한다.     
공식 문서에 따르면 쿼리 메서드 기능은 메서드 네이밍을 분석할 때 엔티티의 프로퍼티, 즉 필드만 참조할 수 있다고 되어 있다.    

