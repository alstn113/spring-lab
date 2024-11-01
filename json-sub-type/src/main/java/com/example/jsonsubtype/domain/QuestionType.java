package com.example.jsonsubtype.domain;

import java.util.Arrays;
import lombok.ToString;

@ToString
public enum QuestionType {

    SHORT_ANSWER,
    MULTIPLE_CHOICE;

    public static QuestionType fromString(String type) {
        return Arrays.stream(values())
                .filter(questionType -> questionType.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown question type: " + type));
    }
}
