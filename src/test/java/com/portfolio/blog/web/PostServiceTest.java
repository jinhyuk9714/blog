package com.portfolio.blog.web;

import com.portfolio.blog.core.MarkdownService;
import com.portfolio.blog.core.SlugService;
import com.portfolio.blog.domain.Post;
import com.portfolio.blog.repo.PostRepository;
import com.portfolio.blog.repo.TagRepository;
import com.portfolio.blog.service.PostService;
import com.portfolio.blog.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({PostService.class, TagService.class, MarkdownService.class, SlugService.class})
class PostServiceTest {

    @Autowired PostService posts;
    @Autowired TagRepository tagRepo;
    @Autowired PostRepository postRepo;

    private Post make(String title, String md) {
        Post p = new Post();
        p.setTitle(title);
        p.setContentMd(md);
        return p;
    }

    @Test @DisplayName("생성 시 마크다운 렌더 & 슬러그 생성")
    void create_renders_md_and_slug() {
        var saved = posts.create("Hello World", "# Title",
                Set.of("Spring","REST"), true);
        assertThat(saved.getSlug()).isEqualTo("hello-world");
        assertThat(saved.getContentHtml()).contains("<h1>Title</h1>");
        assertThat(saved.isPublished()).isTrue();
        assertThat(tagRepo.findAll()).hasSize(2);
    }

    @Test @DisplayName("슬러그 유일성 충돌 → -2, -3 증가")
    void slug_unique() {
        posts.create("Hello World", "a", Set.of(), true);
        var s2 = posts.create("Hello World", "b", Set.of(), true);
        var s3 = posts.create("Hello World", "c", Set.of(), true);
        assertThat(s2.getSlug()).isEqualTo("hello-world-2");
        assertThat(s3.getSlug()).isEqualTo("hello-world-3");
    }

    @Test @DisplayName("공개 검색 목록")
    void list_published() {
        posts.create("Spring Tip", "a", Set.of(), true);
        posts.create("Hidden", "a", Set.of(), false);
        var page = posts.searchPublished("spring", PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Spring Tip");
    }
}