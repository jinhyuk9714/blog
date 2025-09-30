// File: src/main/java/com/portfolio/blog/core/MarkdownService.java
package com.portfolio.blog.core;

import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarkdownService {

    private final Parser parser = Parser.builder()
            .extensions(List.of(TablesExtension.create()))
            .build();

    private final HtmlRenderer renderer = HtmlRenderer.builder()
            .softbreak("\n") // why: OS/환경 상관없이 줄바꿈 고정
            .attributeProviderFactory(new AttributeProviderFactory() {
                @Override public AttributeProvider create(AttributeProviderContext context) {
                    return (node, tagName, attributes) -> {
                        // why: ```java → class="language-java"
                        if (node instanceof FencedCodeBlock fcb) {
                            String lang = fcb.getInfo();
                            if (lang != null && !lang.isBlank()) {
                                attributes.put("class", "language-" + lang.trim());
                            }
                        }
                    };
                }
            })
            .build();

    /** 마크다운 → HTML (테스트 친화적으로 개행·공백 normalize) */
    public String render(String markdown) {
        if (markdown == null || markdown.isBlank()) return "";
        Node doc = parser.parse(markdown);
        String html = renderer.render(doc);

        // --- Normalize: 테스트 문자열 정확히 맞추기 ---
        html = html.replace("\r\n", "\n");             // CRLF → LF
        html = html.replaceAll("[ \t]+(\n)", "$1");    // 줄 끝 공백 제거
        html = html.replaceAll("\n{3,}", "\n\n");      // 과도한 빈줄 2줄로 축소
        if (!html.endsWith("\n")) html = html + "\n";  // 항상 마지막 개행 보장
        return html;
    }

    /** 호환 alias */
    public String toHtml(String markdown) { return render(markdown); }
}
