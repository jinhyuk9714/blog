// File: src/test/java/com/portfolio/blog/web/MarkdownServiceTest.java
package com.portfolio.blog.core;

import com.portfolio.blog.core.MarkdownService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

class MarkdownServiceTest {

    private final MarkdownService service = new MarkdownService();

    @Test
    void heading_bold_code_list_to_html() {
        String md = """
                # 제목
                본문 **굵게**와 *기울임* 입니다.
                
                - 아이템1
                - 아이템2
                
                ```java
                System.out.println("Hi");
                ```
                """;

        String html = service.render(md);

        // 실패 시 실제 HTML을 로그로 보여주기
        try {
            Document doc = Jsoup.parse(html);

            // 1) 헤딩 텍스트
            assertThat(doc.selectFirst("h1")).withFailMessage("h1 없음\n===HTML===\n%s", html).isNotNull();
            assertThat(doc.selectFirst("h1").text()).isEqualTo("제목");

            // 2) 본문 문장(굵게/기울임이 포함된 문장)
            //   * p가 여러 개일 수 있어 모든 p를 합쳐서 체크
            String allP = doc.select("p").text();
            assertThat(allP).contains("본문");
            assertThat(doc.select("strong").text()).contains("굵게");
            assertThat(doc.select("em").text()).contains("기울임");

            // 3) 리스트 2개
            assertThat(doc.select("ul > li")).hasSize(2);
            assertThat(doc.select("ul > li").get(0).text()).isEqualTo("아이템1");
            assertThat(doc.select("ul > li").get(1).text()).isEqualTo("아이템2");

            // 4) 코드 블록: class="language-java" + 내부 텍스트 (엔티티 해제된 값으로 비교)
            Element code = doc.selectFirst("pre > code");
            assertThat(code).withFailMessage("code 블록 없음\n===HTML===\n%s", html).isNotNull();
            // class는 있을 수도/없을 수도 있으니 있으면 자바인지 확인(없으면 스킵)
            if (!code.classNames().isEmpty()) {
                assertThat(code.classNames()).contains("language-java");
            }
            // Jsoup는 code.text() 에서 &quot; → " 로 디코딩됨
            assertThat(code.text()).contains("System.out.println(\"Hi\");");

        } catch (AssertionError e) {
            // 위에서 못 잡은 경우를 위해 마지막 보호막으로 전체 HTML을 보여줌
            fail("Markdown HTML 검증 실패\n===HTML===\n%s\n===ERROR===\n%s", html, e.getMessage());
        }
    }
}