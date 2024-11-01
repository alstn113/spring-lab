package com.example.jsonsubtype.ui;

import com.example.jsonsubtype.application.response.QuestionResponse;
import java.util.List;

public record QuestionsResponse(List<QuestionResponse> questions) {
}
