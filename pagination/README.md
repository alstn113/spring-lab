# 페이지네이션의 문제 상황들과 해결 방법을 알아보자

## 참고

- [tecoble fetch join pagination](https://tecoble.techcourse.co.kr/post/2020-10-21-jpa-fetch-join-paging/)
- [fetch join + batch size](https://daydayplus.tistory.com/105)
- [Querydsl 사용법](https://ililil9482.tistory.com/185)
- [우아한 형제들 Querydsl](https://velog.io/@youngerjesus/우아한-형제들의-Querydsl-활용법)
- [offset 방식이 느린 이유](https://wonit.tistory.com/664)
- [no offset(cursor) 방식 - 향로](https://jojoldu.tistory.com/528)
- [메모리에서 페이징 막기](https://jojoldu.tistory.com/737)

## 페이지네이션이란?

offset 방식과 no offset 방식이 있다. no offset 방식은 cursor 방식이라고도 한다.
offset 방식은 해당 위치까지 모든 데이터를 읽어야하기 때문에 데이터가 많을 경우 성능이 떨어진다.
cursor 방식은 해당 위치부터 데이터를 읽기 때문에 성능이 좋다.

## offset 방식

join fetch와 같이 사용할 경우 limit이 작동하지 않고, 모든 데이터를 메모리에 올린 후 limit을 적용한다.
그렇기 때문에 OutOfMemoryError가 발생할 수 있다.

> 1:N 관계에서 fetch join이 작동하지 않는데 N부분에서 읽고 limit을 적용하면 해결할 수도 있다.

해결 방법으로 default_batch_fetch_size를 사용할 수 있다.
join fetch를 사용하지 않고, batch size를 적용하면 limit이 작동하고, join부분은 batch size만큼씩 가져온다.
1:N:N:N... 관계에서도 잘 작동한다.

복잡한 관계에서 fetch join 후 batch size를 적용할 수도 있다.
실험 결과 post(100) * (comment)10 * (reaction)10 * (log)10 개수, batch size=500, page size=10일 때
comment를 fetch join 적용하면 106ms, 적용하지 않으면 32ms가 걸린다. 일단 batch size만 하는게 좋은 것 같다.

## no offset 방식
