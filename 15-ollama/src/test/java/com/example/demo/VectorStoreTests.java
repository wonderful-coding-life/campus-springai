package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
@Slf4j
public class VectorStoreTests {
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private VectorStore vectorStore;

    @Test
    public void testMarkdownReader() throws IOException {
        var config = MarkdownDocumentReaderConfig.builder()
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .withHorizontalRuleCreateDocument(true)
                .withAdditionalMetadata("category", "markdown")
                .build();

        var reader = new MarkdownDocumentReader("classpath*:*.md", config);

        List<Document> documents = reader.get();
        vectorStore.write(documents);
    }

    @Test
    public void testSimilaritySearch() {
        //String question = "제가 교재를 구매했는데 책에 필기를 조금 했습니다. 반품하려면 배송비는 누가 부담하고 환불은 받을 수 있나요?";
        String question = "쿠폰과 적립금을 사용해서 결제했는데 일부 상품만 반품하면 환불 금액은 어떻게 계산되나요?";
        //String question = "주문한 상품과 다른 상품이 배송됐는데 반품 절차와 환불까지 걸리는 시간을 알려주세요.";

        var completion = chatClient.prompt()
                .system("당신은 캠퍼스 쇼핑몰 고객센터 상담원이야. 친절하고 명확하며 간략하게 답변 해 줘.")
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder()
                                .query(question)
                                .similarityThreshold(0.7)
                                .topK(2)
                                .filterExpression("category == 'markdown'")
                                .build())
                        .build())
                .advisors(SimpleLoggerAdvisor.builder().build())
                .user(question)
                .call().content();

        log.info("\ncompletion = {}", completion);
    }
}
