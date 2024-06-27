package com.project.requestscope.advanced;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AdvancedControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("ArgumentResolver 에만 인증 로직이 작동한다.")
    void testArgumentResolver() {
        RestAssured.given().log().all()
                .header(AUTHORIZATION, "Bearer 1")
                .when().get("/a")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("Interceptor 에만 인증 로직이 작동한다.")
    void testInterceptor() {
        RestAssured.given().log().all()
                .header(AUTHORIZATION, "Bearer 1")
                .when().get("/check/a")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("ArgumentResolver 와 Interceptor 에 인증 로직이 작동한다.")
    void testArgumentResolverAndInterceptor() {
        RestAssured.given().log().all()
                .header(AUTHORIZATION, "Bearer 1")
                .when().get("/check/b")
                .then().log().all()
                .statusCode(200);
    }
}
