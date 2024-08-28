# JPA 값 타입에 대해 알아보자

## 참고

- [김영한 JPA 값 타입 정리](https://velog.io/@jhp1115/About-%EA%B0%92-%ED%83%80%EC%9E%85)
- [김영한 JPA 정리 블로그](https://sudo-minz.tistory.com/145)
- [일급 컬렉션 예시](https://github.com/jaeyeonling/reaction-game/blob/main/src/main/java/reactiongame/domain/Reactions.java)

## 엔티티 타입 vs 값 타입

- 엔티티 타입: @Entity로 정의하는 객체, pk 값으로 관리되기 때문에 관리가 되기 때문에 데이터가 변해도 식별자로 지속적으로 추적이 가능하고 관리도 편하다.
- 값 타입: 기본 타입(int, String 등)이나 래퍼 클래스(Integer 등)와 같이 식별자가 없는 객체이다. 값만 있으므로 값이 변하면 추적이 어렵다.

## 기본값 타입

기본 타입(int, double)이나 래퍼 클래스(String, Integer)와 같은 타입을 말한다.
생명 주기가 엔티티에 의존적이다. 엔티티가 삭제되면 값 타입도 삭제된다.

## 임베디드 타입

@Embeddable로 정의된 클래스 사용. 엔티티의 일부로 사용되는 타입이다.
같은 타입을 여러 곳에서 사용할 수 있다. AttributeOverrides를 사용하여 컬럼명을 재정의할 수 있다.
관련된 정보를 하나의 객체로 묶어서 관리할 수 있다. 불변 객체로 만들어서 사용해야 한다.

## 컬렉션 값 타입

여러 값을 저장할 때 사용. @ElementCollection과 @CollectionTable을 이용하여 테이블을 생성.
컬렉션 변경 후 저장할 때는 JPA가 추적하지 못하므로, 변경 후에는 다시 저장해야 한다.
List의 경우 컬렉션을 변경할 때 모두 지우고, 새로 저장해야 한다. 이는 성능 저하를 초래할 수 있다. Set은 잘 된다.
왜냐하면 값 타입의 경우 엔티티와 다르게 식별자 개념이 없기 때문이다. 식별자 pk가 조합으로 이루어져서 조회가 어렵다.

실무에서는 여러 제약으로 인해 사용을 지양한다. 대신 일대다를 사용한다.
@OneToMany(cascade = CascadeType.ALL, orphanRomoval = true)를 사용하면 컬렉션 값 타입처럼 사용할 수 있다.

## 일급 컬렉션

컬렉션 값 타입을 사용하면 컬렉션을 하나의 값으로 사용할 수 있다.
이때 컬렉션을 감싸서 컬렉션을 사용하는 것을 일급 컬렉션이라고 한다.
이거를 분리해서 사용하자!
