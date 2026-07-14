package com.example.demo.controller;

import com.example.demo.tool.ProductOrderTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ApiController {
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ProductOrderTool productOrderTool;

    private static final String systemMessage = """
        당신은 캠퍼스 쇼핑몰의 고객지원 상담사입니다.
        다음 원칙에 따라 답변하세요.
        1. 확인된 사실에 근거하여 정확하게 답변합니다.
        2. 제공된 정보만으로 답변할 수 없거나 확실하지 않은 내용은 추측하지 않습니다.
        3. 답변하기 어려운 문의는 고객센터 02-500-5000으로 안내합니다.
        4. 답변은 짧고 명확하게 작성합니다.
        5. 답변은 순수 텍스트(Plain Text) 형식으로 작성하며, Markdown 문법은 사용하지 않습니다.
        6. 사용자가 인사만 하거나 구체적인 문의 없이 말을 건 경우에는 간단히 인사한 뒤 문의 내용을 요청합니다.
        7. 문의 내용을 요청할 때는 문의 유형이나 예시를 나열하지 않습니다.
        """;

    @PostMapping("/chats")
    public String postChats(@RequestBody String message) {
        return chatClient.prompt()
                .system(systemMessage)
                .user(message)
                .tools(productOrderTool)
                .toolContext(Map.of("username", "seojun"))
                .call().content();
    }
}
