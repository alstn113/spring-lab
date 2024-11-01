package com.example.jsonsubtype.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Question {

    private final String text;
    private final QuestionType type;
    private final String correctAnswer;
    private final List<String> options = new ArrayList<>();

    public static Question shortAnswer(String text, String correctAnswer) {
        return new Question(text, QuestionType.SHORT_ANSWER, correctAnswer, List.of());
    }

    public static Question multipleChoice(String text, List<String> options) {
        return new Question(text, QuestionType.MULTIPLE_CHOICE, null, options);
    }

    public Question(String text, QuestionType type, String correctAnswer, List<String> options) {
        this.text = text;
        this.type = type;
        this.correctAnswer = correctAnswer;
        this.options.addAll(options);
    }
}
