package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
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
        //doChatResponse();
        //doChatOptions();
        doOpenAiChatOptions();
    }

    private void doChatResponse() {
        var chatResponse = chatClient.prompt()
                .user("유럽에서 가장 인구가 많은 나라는 어디인가요?")
                .call().chatResponse();

        log.info("completion = {}", chatResponse.getResult().getOutput().getText());
        log.info("model = {}", chatResponse.getMetadata().getModel());
        var usage = chatResponse.getMetadata().getUsage();
        log.info("usage = {}", usage);
        var rateLimit = chatResponse.getMetadata().getRateLimit();
        log.info("rateLimit = {}", rateLimit);
    }

    private void doChatOptions() {
        var completion = chatClient.prompt()
                .options(ChatOptions.builder()
                        .model("gpt-4o")
                        .temperature(0.8)
                        .topP(0.3))
                .user("커피를 마시고 싶은 마음이 들게 하는 한줄 광고 문구를 만들어줘")
                .call().content();
        log.info("completion = {}", completion);
    }

    private void doOpenAiChatOptions() {
        var results = chatClient.prompt()
                .options(OpenAiChatOptions.builder()
                        .model("gpt-5.6-terra")
                        .n(1)
                        .serviceTier("default") // default, flex, priority
                        .reasoningEffort("none") // none, low, medium, high, xhigh, max
                        .temperature(1.0)
                        .topP(1.0))
                .user("최근 AI 산업의 흐름을 볼 때, 우리나라 경제와 산업에는 어떤 영향이 있을까요?")
                .call().chatResponse().getResults();

        results.forEach(result -> {
            log.info("completion = {}", result.getOutput().getText());
        });
    }



}
