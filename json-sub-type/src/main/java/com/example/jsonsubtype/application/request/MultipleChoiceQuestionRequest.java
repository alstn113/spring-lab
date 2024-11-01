package com.example.jsonsubtype.application.request;

import java.util.List;

public record MultipleChoiceQuestionRequest(
        String questionText,
        String questionType,
        AdditionalData additionalData
) implements QuestionRequest {

    public record AdditionalData(List<String> options) {
    }
}
