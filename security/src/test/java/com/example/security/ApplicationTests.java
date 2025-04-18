package com.example.security;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.example.security.app.application.AuthService;
import com.example.security.app.application.request.LoginRequest;
import com.example.security.app.application.request.RegisterRequest;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @BeforeAll
    static void init(@Autowired AuthService authService) {
        authService.register(new RegisterRequest(USERNAME, PASSWORD));
    }

    @Test
    void testPublicEndpoint() {
        given().log().all()
                .get("/public")
                .then().log().all()
                .statusCode(200)
                .body(equalTo("Test endpoint is working!"));
    }

    @Test
    void testPrivateEndpoint() {
        Cookie cookie = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new LoginRequest(USERNAME, PASSWORD))
                .post("/login")
                .getDetailedCookie("access_token");

        given().log().all()
                .cookie(cookie)
                .get("/private")
                .then().log().all()
                .statusCode(200)
                .body(equalTo("Hello! you are 1"));
    }

    @Test
    void testPrivateEndpointWithNoCookie() {
        given().log().all()
                .get("/private")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    void testPrivateEndpointWithInvalidToken() {
        given().log().all()
                .cookie("access_token", "invalid_token")
                .get("/private")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    void testPrivateEndpoint2() {
        Cookie cookie = given().log().all()
                .contentType("application/json")
                .body(new LoginRequest(USERNAME, PASSWORD))
                .post("/login")
                .getDetailedCookie("access_token");

        given().log().all()
                .cookie(cookie)
                .get("/private/2")
                .then().log().all()
                .statusCode(200)
                .body(equalTo("Hello! you are 1"));
    }
}
