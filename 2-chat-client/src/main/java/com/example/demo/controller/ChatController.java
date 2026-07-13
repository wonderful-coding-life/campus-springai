package com.example.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    @Autowired
    private ChatModel chatModel;

    @PostMapping("/chats/model")
    public String postChatModel(@RequestBody String message) {
        return chatModel.call(message);
    }

    @Autowired
    private ChatClient chatClient;

    @PostMapping("/chats/client")
    public String postChatClient(@RequestBody String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
