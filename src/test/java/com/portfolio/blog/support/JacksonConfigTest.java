// File: src/test/java/com/portfolio/blog/support/JacksonConfigTest.java
package com.portfolio.blog.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * WHY: ObjectMapper 빈이 정상 주입되는지, 전역 설정 충돌 탐지용 가벼운 스모크
 */
@SpringBootTest
class JacksonConfigTest {
    @Autowired
    ObjectMapper om;

    @Test
    void objectMapperLoads() {
        assertThat(om).isNotNull();
    }
}
