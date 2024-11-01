package com.example.jsonsubtype;

import com.example.jsonsubtype.application.request.CreateQuestionsRequest;
import com.example.jsonsubtype.application.request.MultipleChoiceQuestionRequest;
import com.example.jsonsubtype.application.request.MultipleChoiceQuestionRequest.AdditionalData;
import com.example.jsonsubtype.application.request.ShortAnswerQuestionRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionControllerTest {

    @LocalServerPort
    protected int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("다양한 문제 타입들을 응답받을 수 있다.")
    void getAll() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .get("/questions")
                .then()
                .statusCode(200)
                .log()
                .all();
    }

    @Test
    @DisplayName("다양한 타입의 문제들을 응답받아 생성할 수 있다.")
    void createQuestions() {
        CreateQuestionsRequest request = new CreateQuestionsRequest(List.of(
                new ShortAnswerQuestionRequest("주관식 문제", "SHORT_ANSWER", "문제 정답"),
                new MultipleChoiceQuestionRequest("객관식 문제", "MULTIPLE_CHOICE", new AdditionalData(
                        List.of("보기1", "보기2", "보기3", "보기4"), 1
                ))
        ));

        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .log().all()
                .post("/questions")
                .then()
                .statusCode(200)
                .log()
                .all();
    }

    @Test
    @DisplayName("없는 타입의 문제를 요청 받을 시 예외를 발생한다.")
    void createQuestionsWithInvalidType() {
        CreateQuestionsRequest request = new CreateQuestionsRequest(List.of(
                new ShortAnswerQuestionRequest("주관식 문제", "INVALID_TYPE", "문제 정답")
        ));
        String requestString = request.toString();

        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(requestString)
                .when()
                .post("/questions")
                .then()
                .statusCode(400)
                .log()
                .all();
    }
}