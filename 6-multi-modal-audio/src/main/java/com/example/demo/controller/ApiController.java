package com.example.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ApiController {
    @Autowired
    private ChatClient chatClient;

    @PostMapping("/meetings")
    public String postMeetings(@RequestParam("file") MultipartFile file) {
        var media = Media.builder()
                .data(file.getResource())
                .mimeType(normalizeAudioMimeType(file.getContentType()))
                .build();
        return chatClient.prompt()
                .user(spec -> spec
                        .text("회의 내용을 한국어로 요약해 주세요")
                        .media(media))
                .call().content();
    }

    private MimeType normalizeAudioMimeType(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("파일의 MIME 타입을 확인할 수 없습니다.");
        }

        return switch (contentType.toLowerCase()) {
            case "audio/mpeg", "audio/mp3" ->
                    MimeTypeUtils.parseMimeType("audio/mp3");

            case "audio/wav", "audio/wave",
                 "audio/x-wav", "audio/vnd.wave" ->
                    MimeTypeUtils.parseMimeType("audio/wav");

            default ->
                    throw new IllegalArgumentException(
                            "지원하지 않는 오디오 형식입니다: " + contentType);
        };
    }
}
