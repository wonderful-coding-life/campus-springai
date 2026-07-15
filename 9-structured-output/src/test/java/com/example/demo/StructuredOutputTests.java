package com.example.demo;

import com.example.demo.ocr.ReceiptOcrBatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.MimeTypeUtils;

import java.text.MessageFormat;

@SpringBootTest
@Slf4j
public class StructuredOutputTests {
    @Autowired
    private OpenAiChatModel chatModel;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void testStructuredOutput() {
        var media = new Media[] {
                Media.builder()
                        .data(resourceLoader.getResource("classpath:image/receipt-1.jpg"))
                        .mimeType(MimeTypeUtils.IMAGE_JPEG).build(),
                Media.builder()
                        .data(resourceLoader.getResource("classpath:image/receipt-1.jpg"))
                        .mimeType(MimeTypeUtils.IMAGE_JPEG).build()
        };

        var converter = new BeanOutputConverter<>(ReceiptOcrBatch.class);

        log.info("\nformat: {}", converter.getFormat());
        log.info("\njsonSchema: {}", converter.getJsonSchema());

        String message = MessageFormat.format("""
                영수증 이미지에서 정보를 추출해 주세요.
                - 날짜는 LocalDate, 시간은 LocalTime, 날짜시간은 LocalDateTime 형식으로 바꿔 주세요.
                - 이미지에서 확인할 수 없는 값은 추측하지 말고 null로 반환하세요.
                추출한 정보는 다음의 형식으로 작성해 주세요.
                {0}
                """, converter.getFormat());

        var userMessage = UserMessage.builder()
                .text(message)
                .media(media)
                .build();
        var chatResponse = chatModel.call(new Prompt(userMessage));
        var json = chatResponse.getResult().getOutput().getText();
        var receiptOcrs = converter.convert(json);

        log.info("\n{}", receiptOcrs);
    }

    @Autowired
    private ChatClient chatClient;

    @Test
    public void testChatClientEntity() {
        var media = new Media[] {
                Media.builder()
                        .data(resourceLoader.getResource("classpath:image/receipt-1.jpg"))
                        .mimeType(MimeTypeUtils.IMAGE_JPEG).build(),
                Media.builder()
                        .data(resourceLoader.getResource("classpath:image/receipt-1.jpg"))
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
