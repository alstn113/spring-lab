package com.example.jsonsubtype.ui;

import com.example.jsonsubtype.application.QuestionService;
import com.example.jsonsubtype.application.request.CreateQuestionsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/questions")
    public ResponseEntity<Void> create(@RequestBody CreateQuestionsRequest request) {
        questionService.create(request);

        return ResponseEntity.ok().build();
    }
}
