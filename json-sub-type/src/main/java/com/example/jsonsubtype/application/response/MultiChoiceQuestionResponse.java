package com.example.jsonsubtype.application.response;

import java.util.List;

public record MultiChoiceQuestionResponse(
        String questionText,
        String questionType,
        AdditionalData additionalData
) implements QuestionResponse {

    public record AdditionalData(List<String> options) {
    }
}