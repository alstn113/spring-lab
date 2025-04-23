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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

    private static final String MEMBER_USERNAME = "member_username";
    private static final String MEMBER_PASSWORD = "member_password";
    private static final String ADMIN_USERNAME = "admin_username";
    private static final String ADMIN_PASSWORD = "admin_password";

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @BeforeAll
    static void init(@Autowired AuthService authService) {
        authService.register(new RegisterRequest(MEMBER_USERNAME, MEMBER_PASSWORD, "MEMBER"));
        authService.register(new RegisterRequest(ADMIN_USERNAME, ADMIN_PASSWORD, "ADMIN"));
    }

    @Test
    @DisplayName("'/public/**' GET 요청은 인증 없이 접근 가능하다.")
    void testPublicEndpoint() {
        given().log().all()
                .get("/public")
                .then().log().all()
                .statusCode(200)
                .body(equalTo("Public endpoint."));
    }

    @Test
    @DisplayName("'/public/**' 은 POST 요청은 인증이 필요 하다.")
    void testPublicPostEndpoint() {
        given().log().all()
                .post("/public")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("'/private/member/**' 은 Member 권한을 가진 사람만 접근 가능하다.")
    void testPrivateMemberEndpoint() {
        Cookie cookie = getMemberAccessTokenCookie();

        given().log().all()
                .cookie(cookie)
                .get("/private/member")
                .then().log().all()
                .statusCode(200)
                .body(equalTo("Member #1"));
    }

    @Test
    @DisplayName("'/private/member/holder' 은 SecurityContextHolder 에서 인증 정보를 가져온다")
    void testPrivateMemberHolderEndpoint() {
        Cookie cookie = getMemberAccessTokenCookie();

        given().log().all()
                .cookie(cookie)
                .get("/private/member/holder")
                .then().log().all()
                .statusCode(200)
                .body(equalTo("Member #1"));
    }

    @Test
    @DisplayName("'/private/member/**' 은 쿠키가 없을 경우(인증이 안된 경우) 401 예외를 반환한다.")
    void testPrivateMemberEndpointWithoutCookie() {
        given().log().all()
                .get("/private/member")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("'/private/member/**' 은 토큰이 잘못된 경우 401 예외를 반환한다.")
    void testPrivateMemberEndpointWithInvalidToken() {
        given().log().all()
                .cookie("access_token", "invalid_token")
                .get("/private/member")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("'/private/admin/**' 은 ADMIN 권한을 가진 사람만 접근 가능하다.")
    void testPrivateEndpoint2() {
        Cookie cookie = getAdminAccessTokenCookie();

        given().log().all()
                .cookie(cookie)
                .get("/private/admin")
                .then().log().all()
                .statusCode(200)
                .body(equalTo("Admin #2"));
    }

    @Test
    @DisplayName("'/private/admin/**' 은 MEMBER 권한을 가진 사람은 접근 불가능, 403 예외를 반환한다.")
    void testPrivateEndpointWithMember() {
        Cookie cookie = getMemberAccessTokenCookie();

        given().log().all()
                .cookie(cookie)
                .get("/private/admin")
                .then().log().all()
                .statusCode(403);
    }

    private Cookie getMemberAccessTokenCookie() {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new LoginRequest(MEMBER_USERNAME, MEMBER_PASSWORD))
                .post("/login")
                .getDetailedCookie("access_token");
    }

    private Cookie getAdminAccessTokenCookie() {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new LoginRequest(ADMIN_USERNAME, ADMIN_PASSWORD))
                .post("/login")
                .getDetailedCookie("access_token");
    }
}
