# 연관관계에서 자식들을 삭제하는 여러 방법들을 알아보자

- V1: CASCADE.REMOVE와 oprhanRemoval=true를 통해 자식 객체들을 삭제하는 방법
- V2: `@OnDelete`를 통해 DDL에 on delete cascade를 추가해 자식 객체들을 삭제하는 방법
- V3: deleteAll과 deleteInBatch의 차이를 알아보고, 자식 객체들을 삭제하는 방법
- V4: JPQL과 `@Modifying(clearAutomatically = true)`를 통해 자식 객체들을 벌크 삭제하는 방법
