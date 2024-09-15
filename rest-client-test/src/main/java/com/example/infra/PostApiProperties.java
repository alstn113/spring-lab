package com.example.infra;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("post.api")
public record PostApiProperties(
        @NotBlank
        String url
) {
}
