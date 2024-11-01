package com.example.jsonsubtype.application.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = As.EXISTING_PROPERTY, // 기본으로 하면 questionType이 또 추가된다.
        property = "questionType", // questionType을 기준으로 구분한다.
        visible = true // questionType이 보이게 설정한다. 아니면 null로 설정됨.
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ShortAnswerQuestionRequest.class, name = "SHORT_ANSWER"),
        @JsonSubTypes.Type(value = MultipleChoiceQuestionRequest.class, name = "MULTIPLE_CHOICE")
})
public interface QuestionRequest {

    String questionText();

    String questionType();
}
