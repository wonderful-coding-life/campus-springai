package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
@Slf4j
public class MultiModalTests {
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void testAudioInput() {
        var media = Media.builder()
                .data(resourceLoader.getResource("classpath:audio/bts-un-speech.mp3"))
                .mimeType(MimeTypeUtils.parseMimeType("audio/mp3"))
                .build();
        var completion = chatClient.prompt()
                .user(spec -> spec
                        .text("발표 내용을 한글로 요약해 줘")
                        .media(media))
                .call().content();
        log.info("completion = {}", completion);
    }

    @Test
    public void testAudioOutput() throws IOException {
        var assistantMessage = chatClient.prompt()
                .user("스프링AI에 대해 짧게 요약해 주세요")
                .options(OpenAiChatOptions.builder()
                        .model("gpt-audio") // options()는 defaultOptions()를 대체하기 때문에 반드시 넣어 주어야 한다
                        .outputModalities(List.of("text", "audio"))
                        .outputAudio(new OpenAiChatOptions.AudioParameters(
                                OpenAiChatOptions.AudioParameters.Voice.ONYX,
                                OpenAiChatOptions.AudioParameters.AudioResponseFormat.MP3)))
                .call().chatResponse().getResult().getOutput();

        log.info("completion = {}", assistantMessage.getText());

        var audio = assistantMessage.getMedia().getFirst().getDataAsByteArray();
        Files.write(Paths.get("D:/output/springai-onyx.mp3"), audio);
    }
}
