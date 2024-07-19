# 스프링 oauth 인증에 대해서 알아보자

## 참고

- [github docs](https://docs.github.com/ko/apps/oauth-apps/building-oauth-apps/authorizing-oauth-apps)
- [velog-server-oauth](https://github.com/velopert/velog-server/blob/master/src/routes/api/v2/auth/social/social.ctrl.ts)
- [spring-security-ex1](https://docs.github.com/ko/apps/oauth-apps/building-oauth-apps/authorizing-oauth-apps)
- [spring-security-ex2](https://velog.io/@nefertiri/%EC%8A%A4%ED%94%84%EB%A7%81-%EC%8B%9C%ED%81%90%EB%A6%AC%ED%8B%B0-OAuth2-%EB%8F%99%EC%9E%91-%EC%9B%90%EB%A6%AC)

## OAuth 간단 개념

OAuth는 Open Authorization의 약자로, 인터넷 사용자들이 비밀번호를 제공하지 않고 다른 웹사이트 상의 자원에 접근할 수 있는 권한을 부여할 수 있는 공통적인 수단을 제공하는 개방형 표준이다. 이를 통해 사용자는 자신의 데이터를 제3자에게 제공할 수 있으며, 제3자는 사용자의 데이터에 접근할 수 있는 권한을 부여받을 수 있다.

## OAuth 인증 방식

OAuth는 다양한 인증 방식을 제공한다. 그 중에서도 가장 많이 사용되는 방식은 Authorization Code 방식이다. 이 방식은 다음과 같은 순서로 진행된다.

1. 클라이언트가 사용자에게 인증을 요청한다.
2. 사용자가 인증을 수락하면, 인증 서버는 클라이언트에게 인증 코드를 발급한다.
3. 클라이언트는 인증 코드를 사용하여 액세스 토큰을 요청한다.
4. 인증 서버는 클라이언트에게 액세스 토큰을 발급한다.
5. 클라이언트는 액세스 토큰을 사용하여 자원 서버에 요청을 보낸다.
6. 자원 서버는 클라이언트에게 요청한 자원을 제공한다.
7. 클라이언트는 자원을 사용한다.

## 내가 구현한 방식

깃허브 로그인을 통한 인증을 간단하게 구현해봤다.   
작동 과정은 다음과 같다.   

1. FE가 깃허브 로그인 링크를 클릭한다.
2. 로그인 링크는 BE 서버로 이동하며, BE 서버는 클라이언트 아이디와 스코프, 리다이렉트 URI를 포함한 깃허브 로그인 URL을 생성한다.
3. 2에서 생성한 URL로 리다이렉트한다.
4. 깃허브 로그인 페이지에서 로그인을 하면, 깃허브는 리다이렉트 URI로 인증 코드를 전송한다.
5. BE 서버는 인증 코드를 사용하여 깃허브에 액세스 토큰을 요청한다.
6. BE 서버는 액세스 토큰을 사용하여 깃허브에 사용자 정보를 요청한다. (깃허브 id는 고유한 값이고, node_id는 graphql에서 사용하는 고유한 값이다.)
7. BE는 사용자 정보를 바탕으로 사용자를 생성하거나 불러온다. 
8. 사용자 정보를 바탕으로 JWT 토큰을 생성하여 FE에게 전달한다. FE에게 JWT 토큰을 전달하는 방법은 쿠키를 사용하거나, Query Param을 사용하는 방법이 있다.

## 실행 참고

application.yml에서 github.client-id, github.client-secret를 설정해야 한다.
깃허브 페이지에서 email을 설정하지 않을 경우 email은 null로 들어온다.
