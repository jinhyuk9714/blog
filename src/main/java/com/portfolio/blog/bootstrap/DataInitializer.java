package com.portfolio.blog.bootstrap;

import com.portfolio.blog.domain.Post;
import com.portfolio.blog.domain.User;
import com.portfolio.blog.repo.UserRepository;
import com.portfolio.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final PostService posts;
    @Value("${app.admin.username}")
    String adminUsername;
    @Value("${app.admin.password}")
    String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (users.findByUsername(adminUsername).isEmpty()) {
            var u = new User();
            u.setUsername(adminUsername);
            u.setPassword(encoder.encode(adminPassword));
            u.setRoles(Set.of("ADMIN"));
            users.save(u);
        }
        if (posts.listPublished(null, org.springframework.data.domain.PageRequest.of(0, 1)).isEmpty()) {
            var p = new Post();
            p.setTitle("REST API 포트폴리오 블로그 예시");
            p.setContentMd("""
                    # 환영합니다
                    이 API는 **Spring Boot REST** 샘플입니다.
                    
                    - 공개 목록/상세
                    - 댓글 작성
                    - 관리자 CRUD(HTTP Basic)
                    """);
            posts.createOrUpdate(p, Set.of("Spring", "REST", "Portfolio"), true);
        }
    }
}
