package com.project.auth.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.project.auth.application.TokenProvider;
import com.project.auth.domain.Member;
import com.project.auth.domain.MemberRepository;
import com.project.auth.domain.Provider;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthTestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @SpyBean
    private TokenProvider tokenProvider;

    @BeforeEach
    void environmentSetUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("인증이 필수인 경우 인증한 경우")
    void testAuthRequired() {
        Member member = memberRepository.save(new Member("a", Provider.GITHUB, 1L, "a", "a"));
        BDDMockito.doReturn(member.getId())
                .when(tokenProvider).getMemberId(any());

        ExtractableResponse<Response> extract = RestAssured.given().log().all()
                .cookie("token", "MOCK_TOKEN")
                .when().get("/auth/required")
                .then().log().all()
                .extract();

        AccessorResponse accessor = extract.as(AccessorResponse.class);
        assertThat(accessor.id()).isEqualTo(member.getId());
        assertThat(accessor.guest()).isFalse();
    }

    @Test
    @DisplayName("인증이 필수인 경우 인증하지 않은 경우")
    void testAuthRequiredFail() {
        RestAssured.given().log().all()
                .cookie("token", "MOCK_TOKEN")
                .when().get("/auth/required")
                .then().log().all()
                .statusCode(500);
    }

    @Test
    @DisplayName("인증이 필수가 아닌 경우 인증한 경우")
    void testNotRequired() {
        Member member = memberRepository.save(new Member("a", Provider.GITHUB, 1L, "a", "a"));
        BDDMockito.doReturn(member.getId())
                .when(tokenProvider).getMemberId(any());

        ExtractableResponse<Response> extract = RestAssured.given().log().all()
                .cookie("token", "MOCK_TOKEN")
                .when().get("/auth/not-required")
                .then().log().all()
                .extract();

        AccessorResponse accessor = extract.as(AccessorResponse.class);
        assertThat(accessor.id()).isEqualTo(member.getId());
        assertThat(accessor.guest()).isFalse();
    }

    @Test
    @DisplayName("인증이 필수가 아닌 경우 인증하지 않은 경우")
    void testNotRequiredNotAuth() {
        ExtractableResponse<Response> extract = RestAssured.given().log().all()
                .when().get("/auth/not-required")
                .then().log().all()
                .extract();

        AccessorResponse accessor = extract.as(AccessorResponse.class);
        assertThat(accessor.id()).isEqualTo(-1L);
        assertThat(accessor.guest()).isTrue();
    }

    @Test
    @DisplayName("인증이 필요 없는 경우")
    void testPublic() {
        RestAssured.given().log().all()
                .when().get("/auth/public")
                .then().log().all()
                .statusCode(200);
    }
}
