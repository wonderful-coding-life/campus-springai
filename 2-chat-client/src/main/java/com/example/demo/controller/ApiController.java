package com.example.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    @Autowired
    private ChatModel chatModel;

    @PostMapping("/api/v1/chats")
    public String postChatModel(@RequestBody String message) {
        return chatModel.call(message);
    }

    @Autowired
    private ChatClient chatClient;

    @PostMapping(value = {"/api/v2/chats", "/chats"})
    public String postChatClient(@RequestBody String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
