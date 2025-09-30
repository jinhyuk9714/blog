// ====================================================================
// File: src/main/java/com/portfolio/blog/domain/Tag.java
// 변경점: DB 제약 강화(nullable=false)로 slug 공백 저장 방지
// ====================================================================
package com.portfolio.blog.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false) // ★ nullable=false
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false) // ★ nullable=false
    private String slug;

    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();
}
