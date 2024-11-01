package com.example.jsonsubtype.application;

import com.example.jsonsubtype.application.request.CreateQuestionsRequest;
import com.example.jsonsubtype.domain.Question;
import com.example.jsonsubtype.ui.QuestionsResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionMapper questionMapper;

    public void create(CreateQuestionsRequest request) {
        List<Question> questions = questionMapper.toQuestions(request);

        for (Question question : questions) {
            log.info("Creating question: {}", question);
        }
    }

    public QuestionsResponse getAll() {
        List<Question> questions = List.of(
                Question.shortAnswer("What is the capital of France?", "Paris"),
                Question.multipleChoice("What is the capital of Japan?", List.of("Tokyo", "Kyoto", "Osaka"), 1)
        );

        return questionMapper.toResponses(questions);
    }
}
