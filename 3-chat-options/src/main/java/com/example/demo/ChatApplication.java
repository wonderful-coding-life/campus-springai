package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ChatApplication implements ApplicationRunner {
    @Autowired
    private ChatClient chatClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var chatResponse = chatClient.prompt()
                .options(OpenAiChatOptions.builder()
                        .model("gpt-5.6-terra")
                        .n(2)
                        .serviceTier("default") // default, flex, priority
                        .reasoningEffort("none") // none, low, medium, high, xhigh, max
                        .temperature(1.0)
                        .topP(1.0))
                .user("최근 AI 산업의 흐름을 볼 때, 우리나라 경제와 산업에는 어떤 영향이 있을까요?")
                .call().chatResponse();

        chatResponse.getResults().forEach(result -> {
            log.info("completion = {}", result.getOutput().getText());
        });

        log.info("model = {}", chatResponse.getMetadata().getModel());
        var usage = chatResponse.getMetadata().getUsage();
        log.info("usage = {}", usage);
        var rateLimit = chatResponse.getMetadata().getRateLimit();
        log.info("rateLimit = {}", rateLimit);
    }
}
