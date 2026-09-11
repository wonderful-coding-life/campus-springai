package com.example.demo.controller;

import com.example.demo.tool.KnowledgeSearchTool;
import com.example.demo.tool.ProductOrderTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

@RestController
public class ApiController {
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ChatMemory chatMemory;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private KnowledgeSearchTool knowledgeSearchTool;

    @Autowired
    private ProductOrderTool productOrderTool;

    @Autowired
    private JsonMapper jsonMapper;

    @PostMapping(value = "/chats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> postChats(@RequestBody String message,
                                  @RequestParam("username") String username,
                                  @RequestParam("conversationId") String conversationId) {
        return chatClient.prompt()
                .system("당신은 캠퍼스 쇼핑몰 고객센터 상담원이야. 친절하고 명확하며 간략하게 답변 해 줘.")
                .user(message)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                //.advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .tools(knowledgeSearchTool, productOrderTool)
                .toolContext(Map.of("username", username))
                .stream().content().map(jsonMapper::writeValueAsString);
    }
}
