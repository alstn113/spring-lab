# Refresh Token Rotation에 대해서 알아보자

## 목록

- [Spring Kotlin Server](./refresh-token-rotation/): 인증을 테스트할 수 있는 서버입니다.
  - docker/compose.yml을 통해 Valkey(Redis)와 함께 실행할 수 있습니다.
- [React Client](./rtr-web/): 인증을 테스트할 수 있는 웹입니다.
  - pnpm install && pnpm dev로 실행할 수 있습니다.

## 개요

### 시스템 아키텍처 및 인증/인가 방식

- 아키텍처: 헥사고날 아키텍처를 기반으로 구현되었습니다.
- 인증/인가: Filter를 기반으로 인증 및 인가 처리를 구현했습니다.

### 토큰 관리

- 토큰 종류: JWT 기반의 Access Token과 Refresh Token을 사용합니다.
- 발급 방식: Access Token은 `Authorization Bearer` 방식으로, Refresh Token은 `HttpOnly Secure Cookie` 방식으로 발급합니다.
- 재발급: Access Token이 만료되면 Refresh Token을 이용해 Access Token을 재발급하며, 이 과정에서 Refresh Token도 함께 재발급하는 Refresh Token Rotation 방식을 적용했습니다.
- 유효성 관리: Redis를 활용하여 현재 유효한 Refresh Token을 기록하고 관리합니다. 만약 재발급 시 기존 Refresh Token과 다르면 토큰 탈취로 간주하고 무효화 처리합니다.

### 기능

- 멀티 로그인: 로그인 시 `familyId`를 부여한 토큰을 발급하여 여러 기기에서 동시에 로그인할 수 있으며, 최대 연결 기기 수를 제한할 수 있습니다.
- 로그아웃: 개별 기기 로그아웃 기능과 모든 기기에서 로그아웃하는 전체 기기 로그아웃 기능을 모두 제공합니다.
