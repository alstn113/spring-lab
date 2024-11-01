package com.example.jsonsubtype.application;

import com.example.jsonsubtype.application.request.CreateQuestionsRequest;
import com.example.jsonsubtype.application.request.MultipleChoiceQuestionRequest;
import com.example.jsonsubtype.application.request.QuestionRequest;
import com.example.jsonsubtype.application.request.ShortAnswerQuestionRequest;
import com.example.jsonsubtype.domain.Question;
import com.example.jsonsubtype.domain.QuestionType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionMapper {

    public List<Question> toQuestions(CreateQuestionsRequest request) {
        return request.questions().stream()
                .map(this::toQuestion)
                .toList();
    }

    public Question toQuestion(QuestionRequest request) {
        QuestionType type = QuestionType.fromString(request.questionType());
        return switch (type) {
            case SHORT_ANSWER -> toQuestion((ShortAnswerQuestionRequest) request);
            case MULTIPLE_CHOICE -> toQuestion((MultipleChoiceQuestionRequest) request);
        };
    }

    public Question toQuestion(ShortAnswerQuestionRequest request) {
        return Question.shortAnswer(
                request.questionText(),
                request.correctAnswer()
        );
    }

    public Question toQuestion(MultipleChoiceQuestionRequest request) {
        return Question.multipleChoice(
                request.questionText(),
                request.additionalData().options()
        );
    }
}
