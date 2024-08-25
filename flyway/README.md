# flyway에 대해서 알아보자

## 간단 개념

flyway는 오픈소스 데이터베이스 마이그레이션 툴이다.    
Git과 같이 변경 사항을 추적하고, 업데이트나 롤백을 쉽게 할 수 있게 도와준다.

프로젝트가 진행되면서 데이터베이스를 여러 환경에서 사용하게 되고, 이를 관리하기 위해 사용한다.
로컬이나 개발 서버에서는 데이터베이스를 초기화하고, 테스트 서버나 운영 서버에서는 마이그레이션을 수행한다.

## 사용법

```
# gradle

dependencies {
    runtimeOnly 'com.mysql:mysql-connector-j'
    implementation 'org.flywaydb:flyway-core'
    implementation 'org.flywaydb:flyway-mysql'
}
```

resources/db/migration 에서 V1__{init}.sql 형식으로 파일을 작성한다.
수정 시에는 V2__{update}.sql 형식으로 파일을 작성한다.

## 참고

node의 prisma와 같이 데이터베이스 스키마를 감지하여 변경 사항을 자동으로 생성하는 기능을 제공할 줄 알았는데 그런거 없다. 찾지말자 ㅠㅠ,,,
