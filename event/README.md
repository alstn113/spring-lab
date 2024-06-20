# 스프링 이벤트에 대해서 알아보자

## 참고 자료

- [트랜잭션 분리 프로젝트 참고](https://tecoble.techcourse.co.kr/post/2022-11-14-spring-event/)
- [사용법 및 장단점 참고](https://mangkyu.tistory.com/292)
- [트랜잭션 고민 참고](https://findstar.pe.kr/2022/09/17/points-to-consider-when-using-the-Spring-Events-feature/)
- [트랜잭션 예외 전파 테스트 참고](https://devpanpan.tistory.com/entry/Spring-Event-%EB%8F%84%EC%9E%85%EA%B8%B0-2-%EB%A6%AC%EC%8A%A4%EB%84%88-%EB%B0%96%EC%9C%BC%EB%A1%9C-%EC%A0%84%ED%8C%8C%EB%90%98%EB%8A%94-%EC%98%88%EC%99%B8%EB%A5%BC-%EC%B2%98%EB%A6%AC%ED%95%98%EB%9D%BC-Spring-Event-Exception-Handling)
- [트랜잭션 예외 테스트 참고](https://dgjinsu.tistory.com/42)
- [트랜잭션 예외 삽질 참고](https://newwisdom.tistory.com/75)


## 왜 쓰는데?

컴포넌트 간에 통신하거나 독립적인 비즈니스 로직을 수행할 때 사용한다.
스프링 이벤트를 사용하면 느슨한 결합을 유지하면서 유연성과 확장성을 높일 수 있다.

## 대충 개념

이벤트를 발행하는 부분과 이벤트를 구독하는 부분이 있다.

대충 다음과 같은 구조를 가진다.

```java
// 이벤트 객체
public record SampleEvent(String message) {
}
```

```java

@Service
public class SampleService {

    private final ApplicationEventPublisher publisher; // 이벤트 발행자

    public SampleService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void doSomething() {
        // 비즈니스 로직 실행

        // 이벤트 발행
        publisher.publishEvent(new SampleEvent("Hello")); // 이벤트 발행
    }
}
```

```java

@Component
public class SampleEventListener {

    @EventListener // 이벤트 구독자
    public void handle(SampleEvent event) {
        System.out.println(event.message());
    }
}
```
### 이벤트 리스너

이벤트 리스너는 멀티 캐스팅 관계이다. 즉, 하나의 이벤트에 여러 리스너가 구독할 수 있다.

### 동기 / 비동기

또한 이벤트는 동기 방식이기 때문에 트랜잭션이 하나의 범위로 묶일 수 있다.    
발행 부분에서 트랜잭션이 시작된 상태라면 리스너에서도 같은 트랜잭션 범위에서 동작한다.

이벤트를 발행했을 때, 해당 이벤트를 여러번 처리하는 경우 하나의 스레드에서 처리된다. 
예외가 발생할 경우, 순서에 따라 영향을 주게 된다. 그래서 비동기 방식을 사용하면 이러한 문제를 해결할 수 있다.

비동기 방식을 이용하려면 @EnableAsync와 @Async를 사용하면 된다.   
전역적으로 설정하려면 ApplicationEventMulticaster를 빈으로 등록하면 된다.   

### 트랜잭션 분리

@TransactionalEventListener를 사용하여 이벤트 시점을 결정할 수 있다.

- TransactionPhase.BEFORE_COMMIT : 커밋 직전에 이벤트 발생
- TransactionPhase.AFTER_COMPLETION : 커밋 완료 후에 이벤트 발생
- TransactionPhase.AFTER_COMMIT : 커밋 완료 후에 이벤트 발생 (default)
- TransactionPhase.AFTER_ROLLBACK : 롤백 완료 후에 이벤트 발생

## 더 알아볼 점

```java
@Async
@Transactional(propagation = Propagation.REQUIRES_NEW)
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handle(SampleEvent event) {
    System.out.println(event.message());
}
```
이 부분에서 @Transactional(propagation = Propagation.REQUIRES_NEW)를 사용해야 구독하는 부분에서 예외가 발생했을 때, 롤백되도록 할 수 있다.
참고로 발행 부분은 롤백되지 않는다.
