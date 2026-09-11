package com.example.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import tools.jackson.databind.json.JsonMapper;

@RestController
public class ApiController {
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private JsonMapper jsonMapper;

    @GetMapping(value="/chats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getChats(@RequestParam("message") String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content()
                .map(jsonMapper::writeValueAsString);
    }

    @PostMapping(value="/chats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> postChats(@RequestBody String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content()
                .map(jsonMapper::writeValueAsString);
    }
}
