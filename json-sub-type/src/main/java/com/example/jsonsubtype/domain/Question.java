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
    private final Integer correctOptionNumber;

    public static Question shortAnswer(String text, String correctAnswer) {
        return new Question(text, QuestionType.SHORT_ANSWER, correctAnswer, List.of(), null);
    }

    public static Question multipleChoice(String text, List<String> options, int correctOptionNumber) {
        return new Question(text, QuestionType.MULTIPLE_CHOICE, null, options, correctOptionNumber);
    }

    public Question(
            String text,
            QuestionType type,
            String correctAnswer,
            List<String> options,
            Integer correctOptionNumber
    ) {
        this.text = text;
        this.type = type;
        this.correctAnswer = correctAnswer;
        this.correctOptionNumber = correctOptionNumber;
        this.options.addAll(options);
    }
}
