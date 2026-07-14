package com.example.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    @Autowired
    private ChatModel chatModel;

    @Autowired
    private ChatMemory chatMemory;

    @PostMapping("/chats/model")
    public String postChatModel(@RequestBody String message, @RequestParam("id") String id) {
        chatMemory.add(id, UserMessage.builder().text(message).build());
        var response = chatModel.call(Prompt.builder().messages(chatMemory.get(id)).build());
        chatMemory.add(id, response.getResult().getOutput());
        return response.getResult().getOutput().getText();
    }

    @Autowired
    private ChatClient chatClient;

    @PostMapping("/chats/client")
    public String postChatClient(@RequestBody String message, @RequestParam("id") String id) {
        return chatClient.prompt()
                //.advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, id))
                .user(message)
                .call().content();
    }
}
