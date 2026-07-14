package com.example.demo;

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
public class MultiModalTests {
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void multiModalTest() {
        var media = Media.builder()
                .mimeType(MimeTypeUtils.IMAGE_JPEG)
                .data(resourceLoader.getResource("classpath:image/car.jpg"))
                .build();
        var completion = chatClient.prompt()
                .user(spec -> spec
                        .text("자동차 모델 이름을 알려 주세요.")
                        .media(media))
                .call().content();
        log.info("completion: {}", completion);
    }
}
