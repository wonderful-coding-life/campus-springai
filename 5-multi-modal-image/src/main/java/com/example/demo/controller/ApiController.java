package com.example.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ApiController {
    @Autowired
    private ChatClient chatClient;

    @PostMapping("/images")
    public String postImages(@RequestParam("message") String message, @RequestParam("file") MultipartFile file) {
        var media = Media.builder()
                .mimeType(MimeTypeUtils.parseMimeType(file.getContentType()))
                .data(file.getResource())
                .build();
        return chatClient.prompt()
                .user(spec -> spec
                        .text(message)
                        .media(media))
                .call().content();
    }

    @PostMapping("/receipts")
    public String postReceipts(@RequestParam("file") List<MultipartFile> files) {
        var media = files.stream().map(file -> Media.builder()
                .mimeType(MimeTypeUtils.parseMimeType(file.getContentType()))
                .data(file.getResource())
                .build()).toArray(Media[]::new);
        return chatClient.prompt()
                .user(spec -> spec
                        .text("영수증의 날짜, 상호, 금액을 표 형태로 정리해 주세요.")
                        .media(media))
                .call().content();
    }
}
