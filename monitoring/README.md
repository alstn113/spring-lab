# 스프링 모니터링에 대해 알아보자

## 참고

- [Docker Compose 예시](https://github.com/docker/awesome-compose/tree/master/prometheus-grafana)
- [블로그](https://peace-developer.tistory.com/16)

## 구성 요소들 간단 개념

- 스프링 액추에이터(Spring Actuator): 스프링 부트 애플리케이션에 내장된 모니터링 및 관리 기능을 제공하는 라이브러리입니다.
    애플리케이션의 상태, 메트릭, 환경 설정 등의 정보를 REST API로 노출합니다.
    주요 기능: 상태 확인, 메트릭 수집, 환경 정보 제공, 로그 확인, 상태 변경 등

- 마이크로미터(Micrometer): 애플리케이션의 메트릭을 수집하고 관리하는 계층화된 모니터링 API 라이브러리입니다.
스프링 액추에이터와 함께 사용되어 다양한 모니터링 시스템(Prometheus, Graphite, StatsD 등)과 연동할 수 있습니다.
메트릭 수집, 태깅, 집계 등의 기능을 제공합니다.

- 프로메테우스(Prometheus): 오픈 소스 모니터링 및 경고 시스템입니다. 마이크로미터를 통해 수집된 애플리케이션 메트릭을 저장하고 쿼리할 수 있습니다.
메트릭 데이터를 시계열 데이터베이스에 저장하고, 강력한 쿼리 언어를 통해 데이터를 분석할 수 있습니다.
경고 기능을 통해 임계값 초과 시 알림을 보낼 수 있습니다.

- 그라파나(Grafana): 데이터 시각화 및 모니터링 플랫폼입니다. 프로메테우스와 연동하여 애플리케이션 메트릭을 대시보드로 구현할 수 있습니다.
다양한 차트 및 시각화 도구를 제공하여 메트릭 데이터를 효과적으로 분석할 수 있습니다.
사용자 권한 관리, 알림 설정 등의 기능을 통해 모니터링 환경을 구축할 수 있습니다.

## 간단 정리

1. 스프링 액추에이터를 통해 애플리케이션의 상태, 메트릭, 환경 설정 등의 정보를 수집합니다.
2. 마이크로미터를 통해 수집된 메트릭을 프로메테우스에 전달합니다.
3. 프로메테우스는 수집된 메트릭을 저장하고 쿼리할 수 있습니다.
4. 그라파나를 통해 프로메테우스에 저장된 메트릭을 시각화하여 대시보드로 구현할 수 있습니다.