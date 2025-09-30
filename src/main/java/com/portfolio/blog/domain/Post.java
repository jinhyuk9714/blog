package com.portfolio.blog.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="posts", indexes=@Index(name="idx_posts_slug", columnList="slug", unique=true))
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private String title;

    @Column(nullable=false, unique=true, length=120)
    private String slug; // URL 식별자

    @Column(columnDefinition="TEXT") private String contentMd;
    @Column(columnDefinition="TEXT") private String contentHtml;

    private boolean published;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="post_tags",
            joinColumns=@JoinColumn(name="post_id"),
            inverseJoinColumns=@JoinColumn(name="tag_id"))
    private Set<Tag> tags = new LinkedHashSet<>();

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @PrePersist void prePersist(){ var now=OffsetDateTime.now(); createdAt=now; updatedAt=now; }
    @PreUpdate  void preUpdate(){ updatedAt=OffsetDateTime.now(); }
}