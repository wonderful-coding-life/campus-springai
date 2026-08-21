package com.example.demo.controller;

import com.example.demo.tool.KnowledgeSearchTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@RestController
public class ApiController {
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ToolCallbackProvider toolCallbackProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KnowledgeSearchTool knowledgeSearchTool;

    private static final String systemMessage = """
            상품 주문과 관련한 문의에 대해서는
            캠퍼스 쇼핑몰의 고객지원 상담사로서 답변은 짥고 명료하게 해 주세요.
            도구 호출, 판단, 분석, 내부 지시, 응답 계획 등 처리 과정을 노출하지 말고, 사용자에게 필요한 최종 답변만 한국어로 출력하세요.
            --------
            파일 생성, 수정, 읽기 작업 시 사용자가 경로를 명시하지 않으면
            항상 다음 디렉토리를 기본 작업 디렉토리로 사용하세요.
            기본 디렉토리: d:/mcp-server
            --------
            사용자가 페이지 생성/수정/삭제를 요청하면 Notion MCP Tool을 사용하여 작업하세요.
            필수 정보가 충분하면 추가 확인 질문 없이 바로 실행하세요.
            새 페이지 생성 시 사용자가 위치를 지정하지 않으면 항상 다음 parent page_id 아래에 생성하세요.
            parent page_id: 3c2597ed70478092a2bbf781dc7afbbd
            """;

    @PostMapping(value = "/chats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> postChats(@RequestBody String message, Authentication authentication) {
        return chatClient.prompt()
                .system(systemMessage)
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, authentication.getName()))
                .tools(knowledgeSearchTool, toolCallbackProvider)
                .toolContext(Map.of("username", authentication.getName()))
                .stream().content().map(objectMapper::writeValueAsString);
    }
}
