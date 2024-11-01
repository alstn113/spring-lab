# SubType을 JSON으로 직렬화, 역직렬화하는 방법을 알아보자

우선 키워드는 @JsonTypeInfo, @JsonSubTypes이다.
@JsonTypeInfo에서 use를 통해 어떤 속성을 사용할 것인지 지정해야한다.
주의할 점으로 필드에 이미 기준 값이 있을 경우 include = As.EXISTING_PROPERTY로 지정해야한다.
또한 기준 값이 분명히 요청된 것 같은데 null인 경우 visible = true로 지정하면 해결할 수 있다.