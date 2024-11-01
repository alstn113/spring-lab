package com.example.jsonsubtype.application;

import com.example.jsonsubtype.application.request.CreateQuestionsRequest;
import com.example.jsonsubtype.application.request.MultipleChoiceQuestionRequest;
import com.example.jsonsubtype.application.request.QuestionRequest;
import com.example.jsonsubtype.application.request.ShortAnswerQuestionRequest;
import com.example.jsonsubtype.application.response.MultiChoiceQuestionResponse;
import com.example.jsonsubtype.application.response.MultiChoiceQuestionResponse.AdditionalData;
import com.example.jsonsubtype.application.response.QuestionResponse;
import com.example.jsonsubtype.application.response.ShortAnswerQuestionResponse;
import com.example.jsonsubtype.domain.Question;
import com.example.jsonsubtype.domain.QuestionType;
import com.example.jsonsubtype.ui.QuestionsResponse;
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
                request.additionalData().options(),
                request.additionalData().correctOptionNumber()
        );
    }

    public QuestionsResponse toResponses(List<Question> questions) {
        return new QuestionsResponse(
                questions.stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    public QuestionResponse toResponse(Question question) {
        QuestionType type = question.getType();
        return switch (type) {
            case SHORT_ANSWER -> toShortAnswerResponse(question);
            case MULTIPLE_CHOICE -> toMultipleChoiceResponse(question);
        };
    }

    private QuestionResponse toShortAnswerResponse(Question question) {
        return new ShortAnswerQuestionResponse(
                question.getText(),
                question.getType().name()
        );
    }

    private QuestionResponse toMultipleChoiceResponse(Question question) {
        return new MultiChoiceQuestionResponse(
                question.getText(),
                question.getType().name(),
                new AdditionalData(question.getOptions())
        );
    }
}
