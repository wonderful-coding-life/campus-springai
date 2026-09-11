package com.example.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private VectorStore vectorStore;

    @PostMapping("/api/v1/chats")
    public String postChats(@RequestBody String message) {
        return chatClient.prompt()
                .system("당신은 캠퍼스 쇼핑몰 고객센터 상담원이야. 친절하고 명확하며 간략하게 답변 해 줘.")
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder()
                                .similarityThreshold(0.7)
                                .topK(2)
                                .filterExpression("category == 'markdown'")
                                .build())
                        .build())
                .user(message)
                .call().content();
    }

    @Autowired
    private ChatMemory chatMemory;

    @PostMapping(value={"/api/v2/chats", "/chats"})
    public String postChatsWithConversationId(@RequestBody String message, @RequestParam("id") String id) {
        return chatClient.prompt()
                .system("당신은 캠퍼스 쇼핑몰 고객센터 상담원이야. 친절하고 명확하며 간략하게 답변 해 줘.")
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder()
                                .similarityThreshold(0.7)
                                .topK(2)
                                .filterExpression("category == 'markdown'")
                                .build())
                        .build())
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, id))
                .user(message)
                .call().content();
    }

    @DeleteMapping("/chats")
    public void deleteChats(@RequestParam("id") String id) {
        chatMemory.clear(id);
    }
}
