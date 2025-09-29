// File: src/main/java/com/portfolio/blog/config/OpenApiConfig.java
package com.portfolio.blog.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.*;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Portfolio Blog REST API",
                version = "1.0.0",
                description = "포트폴리오 블로그 공개/관리자 REST API 문서",
                contact = @Contact(name = "Your Name", email = "you@example.com")
        )
)
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class OpenApiConfig {
    // WHY: OpenAPI 메타 + BasicAuth 보안 스키마 선언
}
