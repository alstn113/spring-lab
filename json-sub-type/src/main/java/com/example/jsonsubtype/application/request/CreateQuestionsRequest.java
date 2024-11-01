package com.example.jsonsubtype.application.request;

import java.util.List;

public record CreateQuestionsRequest(List<QuestionRequest> questions) {
}
