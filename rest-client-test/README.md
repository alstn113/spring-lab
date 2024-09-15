# RestClientTest로 외부 API 요청 테스트를 하는 방법을 알아보자

## 참고

- [RestClientTest 공식 문서 중요!!](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/test/autoconfigure/web/client/RestClientTest.html)
- [향로 블로그](https://jojoldu.tistory.com/341)

## RestClientTest를 왜 사용하나?

restClient나 restTemplate의 응답을 mocking하는 경우 테스트는 통과하지만 실제 서비스에서는 실패할 수 있다.    
@RestClientTest는 요청 받는 쪽을 위한 테스트가 아닌, 요청 하는 쪽 입장에서의 테스트이다.   
임시 서버를 빈으로 생성하기 때문에 원하는 형태의 요청이 오면 원하는 형태의 응답을 줄 수 있다.

## RestClientTest 사용법

우선 RestClient를 사용할 때 RestClient.create를 하지 않는다.    
RestClientTest 공식 문서에 따르면 RestClient.Builder 빈을 주입받아 사용하라고 한다.    
RestTemplate을 사용할 경우 RestTemplateBuilder 빈을 주입받아 사용하라고 한다.

테스트에서는 @RestClientTest 어노테이션을 사용한다.   
RestTemplate의 경우 @AutoConfigureWebClient(registerRestTemplate = true)를 사용한다.   
MockRestServiceServer로 임시 서버를 생성해서 요청을 보내고 응답을 받는다.   
응답 받는 부분은 ObjectMapper를 사용해서 string 형태로 변환한다.

## 외부 요청에 관련된 주의 사항

- 외부 요청의 경우 제어할 수 없는 부분이기도 하고, 커넥션을 가지는 시간이 길어질 수 있으므로 트랜잭셔에 포함하지 않는다.
    - 참고: https://tecoble.techcourse.co.kr/post/2022-09-20-external-in-transaction/
- 예외 처리
- 타임아웃, 재시도, 요청 제한





