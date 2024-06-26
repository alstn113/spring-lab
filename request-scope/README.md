# Request Scope에 대해서 알아보자

## 대충 개념

빈 스코프에는 싱글톤, 프로토타입, 웹 스코프(request, session, socket, application) 등이 있다.   
웹 스코프는 웹 환경에서만 사용할 수 있으며, 스프링이 해당 스코프의 종료시점까지 관리한다. 따라서 종료 메서드가 호출된다.    
request 스코프는 HTTP 요청 하나가 들어오고 나갈 때까지 유지되는 스코프이다. 각각의 HTTP 요청마다 별도의 빈 인스턴스가 생성되고, 관리된다.   

## 사용법

### 1. @Scope("request") 어노테이션 사용

이 상태로만 사용하면 예외가 발생한다.   
스프링 애플리케이션을 실행하는 시점에 싱글톤 빈은 생성해서 주입이 가능하지만, request 스코프 빈은 아직 생성되지 않는다.   
이 빈은 실제 요청이 와야 생성되기 때문에, 스프링 컨테이너가 미리 생성할 수 없다.

#### 해결방법(1) : ObjectProvider

아래와 같이 ObjectProvider를 사용해서 ObjectProvider.getObject()를 호출하는 시점까지 request scope 빈의 생성을 지연할 수 있다.
getObject를 호출하는 시점에는 HTTP 요청이 왔다는 것이 확실하기 때문에, request scope 빈의 생성이 가능하다.   
컨트롤러와 서비스에서 각각 한번씩 따로 호출해도 같은 HTTP 요청이기 때문에 같은 request scope 빈이 사용된다.

```java
private final ObjectProvider<MyLogger> myLoggerProvider;

public void logic(String id) {
    MyLogger myLogger = myLoggerProvider.getObject();
    myLogger.log("service id = " + id);
}
```

#### 해결방법(2) : Proxy

```java
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
```

적용 대상이 클래스면 TARGET_CLASS를 사용하고, 인터페이스면 INTERFACES를 사용한다.
이렇게 하면 스프링 컨테이너는 MyLogger를 상속받은 가짜 프록시 객체를 만들어서 주입한다.
HTTP 요청과 상관없이 가짜 프록시 클래스를 다른 빈에 미리 주입할 수 있다.

동작 정리
- CGLIB 라이브러리로 가짜 프록시 객체를 만들어서 주입한다.
- 가짜 프록시 객체는 실제 요청이 오면 내부에서 실제 request scope 빈을 요청한다.
- 가짜 프록시 객체는 실제 request scope와는 관계가 없고, 내부에 단순한 위임 로직만 있고, 싱글톤처럼 동작한다.

@RequestScope는 @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)를 대체할 수 있다.
