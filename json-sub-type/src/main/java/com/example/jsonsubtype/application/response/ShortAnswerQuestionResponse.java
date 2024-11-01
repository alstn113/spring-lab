package com.example.jsonsubtype.application.response;

public record ShortAnswerQuestionResponse(
        String questionText,
        String questionType
) implements QuestionResponse {
}
