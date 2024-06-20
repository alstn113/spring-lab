# JPA @Modifying에 대해서 알아보자

@Modifying은 @Query 어노테이션을 사용하여 INSERT, UPDATE, DELETE 쿼리를 실행할 때 사용한다.   
clearAutomatically와 flushAutomatically 속성이 있으며, 주로 벌크 연산을 수행할 때 사용한다.

@Query 어노테이션을 사용하여 JPQL을 실행할 때, @Modifying을 사용하지 않으면 예외가 발생한다.

동일한 트랜잭션 내에서 벌크 연산을 수행한 후, 벌크 연산이 수행된 엔티티를 조회하면 벌크 연산이 반영되지 않은 상태로 조회된다.     
그 이유는 벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리를 날리고, 조회할 때는 영속성 컨텍스트를 우선하여 조회하기 때문이다.    
clearAutomatically 속성을 true로 설정하면 벌크 연산 후 영속성 컨텍스트를 초기화하여 벌크 연산이 반영된 상태로 조회할 수 있다.    

가장 좋은 해결책은 트랜잭션 내에서 벌크 연산만을 수행하는 것 같다.   

flushAutomatically를 true로 하지 않아도 벌크 연산 전 영속성 컨텍스트를 flush하여 영속성 컨텍스트와 데이터베이스를 동기화한다.    
그 이유는 Hibernate의 flushModeType이 AUTO로 설정되어 있기 때문이다.    
