# MDC 사용법

## MDC란?

MDC는 "Mapped Diagnostic Context"의 약자로, 로그 메시지에 추가적인 컨텍스트 정보를 추가할 수 있는 기능을 제공합니다.    
Spring과 같은 Java 기반 애플리케이션에서 로그를 남길 때, MDC를 사용하면 로그 메시지에 특정한 컨텍스트 정보를 포함시킬 수 있습니다.

## MDC 사용법

AOP나 Filter를 사용하여 MDC에 값을 추가하고, 로그를 남길 때 MDC에 저장된 값을 함께 출력할 수 있습니다.
주의할 점으로 MDC에 값을 추가한 후에는 반드시 제거해주어야 합니다.

```java

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public final class MdcLoggingFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain
    ) throws IOException, ServletException {
        try {
            MDC.put("requestId", UUID.randomUUID().toString());
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.clear();
        }
    }
}
```

## 로그에서 MDC 사용하기

MDC에 넣은 값을 로그에서 사용하려면, 로그 패턴에 `%X{key}`를 사용하면 됩니다. (Logback 기준)

```xml
<pattern>
    %d{yyyy-MM-dd HH:mm:ss.SSS} | %t | %highlight(%-5p) | %cyan(%logger{36}) | %X{requestId} | %m%n
</pattern>
```
