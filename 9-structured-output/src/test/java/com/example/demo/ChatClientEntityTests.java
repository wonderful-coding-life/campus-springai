package com.example.demo;

import com.example.demo.ocr.ReceiptOcrBatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.MimeTypeUtils;

@SpringBootTest
@Slf4j
public class ChatClientEntityTests {
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void testChatClientEntity() {
        var media = new Media[] {
                Media.builder()
                        .data(resourceLoader.getResource("classpath:image/receipt-1.jpg"))
                        .mimeType(MimeTypeUtils.IMAGE_JPEG).build(),
                Media.builder()
                        .data(resourceLoader.getResource("classpath:image/receipt-2.jpg"))
                        .mimeType(MimeTypeUtils.IMAGE_JPEG).build()
        };

        var completion = chatClient.prompt()
                .user(spec -> spec
                        .text("""
                                영수증 이미지에서 정보를 추출해 주세요.
                                - 날짜는 LocalDate, 시간은 LocalTime, 날짜시간은 LocalDateTime 형식으로 바꿔 주세요.
                                - 이미지에서 확인할 수 없는 값은 추측하지 말고 null로 반환하세요.
                                """)
                        .media(media))
                .call()
                .entity(ReceiptOcrBatch.class);

        log.info("\n{}", completion);
    }
}
