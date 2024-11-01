package com.example.jsonsubtype.application.request;

public record ShortAnswerQuestionRequest(
        String questionText,
        String questionType,
        String correctAnswer
) implements QuestionRequest {
}
