// ====================================================================
// File: src/main/java/com/portfolio/blog/bootstrap/DataInitializer.java
// 변경점: 앱 시작 시 관리자 사용자 생성(없을 때만). 기존 데모 글 주입 유지.
// ====================================================================
package com.portfolio.blog.bootstrap;

import com.portfolio.blog.domain.User;
import com.portfolio.blog.repo.UserRepository;
import com.portfolio.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.PageRequest;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final PostService posts;
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;
    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // 1) 관리자 유저 부트스트랩(최초 1회)
        users.findByUsername(adminUsername).orElseGet(() -> {
            var u = new User();
            u.setUsername(adminUsername);
            u.setPassword(passwordEncoder.encode(adminPassword)); // ★ 인코딩
            u.setRoles(Set.of("ADMIN")); // ROLE_ 접두어는 Security가 자동 부여
            return users.save(u);
        });

        // 2) 샘플 포스트(없을 때만)
        var empty = posts.listPublished(PageRequest.of(0, 1)).isEmpty();
        if (empty) {
            posts.create(
                    "REST API 포트폴리오 블로그 예시",
                    """
                    # 환영합니다
                    이 API는 **Spring Boot REST** 샘플입니다.

                    - 공개 목록/상세
                    - 댓글 작성
                    - 관리자 CRUD(HTTP Basic)
                    """,
                    Set.of("Spring","REST","Portfolio"),
                    true
            );
        }
    }
}
