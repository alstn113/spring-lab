# JPA 락에 대해 알아보자

## 참고자료

- [비관적 락](https://isntyet.github.io/jpa/JPA-비관적-잠금(Pessimistic-Lock)/)
- [낙관적 락1](https://velog.io/@znftm97/동시성-문제-해결하기-V1-낙관적-락Optimisitc-Lock-feat.데드락-첫-만남)
- [낙관적 락2](https://devoong2.tistory.com/entry/JPA-에서-낙관적-락Optimistic-Lock을-이용해-동시성-처리하기)
- [둘 다](https://velog.io/@hyojhand/상품-주문-동시성-문제-해결하기-DeadLock-낙관적-락-비관적-락)

## 비관적 락(Pessimistic Lock)

비관적 락은 충돌할 것이라고 가정하고 우선 DB에 락을 거는 방식(select for update)이다.    
트랜잭션은 데이터를 읽을 때 락을 획득하고, 트랜잭션이 끝날 때까지 락을 유지합니다.    
데이터베이스 레벨에서 동시성을 처리한다.

- PESSIMISTIC_READ: 엔티티를 읽을 때 락을 획득합니다. 다른 트랜잭션이 엔티티를 수정하는 것을 막습니다.
- PESSIMISTIC_WRITE: 엔티티를 수정할 때 락을 획득합니다. 다른 트랜잭션이 읽거나 수정하는 것을 막습니다.
- PESSIMISTIC_FORCE_INCREMENT: 엔티티를 읽을 때 버전 필드를 증가시킵니다. 이는 주로 트랜잭션이 데이터를 읽는 동시에 수정이 발생할 가능성을 높이기 위해 사용됩니다.

## 낙관적 락(Optimistic Lock)

낙관적 락은 충돌하지 않을 것이라고 가정하고 작업을 수행하며, @Version을 사용하여 버전을 관리한다.      
버전을 관리하면서 충돌이 발생하면 예외를 발생시킨다.      
데이터베이스 레벨에서 동시성을 처리하는 것이 아닌 애플리케이션 레벨에서 처리한다.

- Optimistic Lock: 엔티티를 수정할 때 버전 필드를 확인하여 충돌 여부를 감지합니다. 트랜잭션이 엔티티를 수정하고 커밋하려고 할 때, 현재 버전 번호가 트랜잭션이 시작될 때의 버전 번호와 다른 경우,
  충돌이 발생했다고 판단하고 예외를 발생시킵니다.
- OPTIMISTIC_FORCE_INCREMENT: 엔티티를 읽을 때 버전 필드를 증가시킵니다. 이는 주로 트랜잭션이 데이터를 읽는 동시에 수정이 발생할 가능성을 높이기 위해 사용됩니다.
