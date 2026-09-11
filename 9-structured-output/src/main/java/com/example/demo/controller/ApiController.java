package com.example.demo.controller;

import com.example.demo.ocr.ReceiptOcrBatch;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ApiController {
    @Autowired
    private ChatClient chatClient;

    @PostMapping("/receipts")
    public ReceiptOcrBatch postReceipts(@RequestParam("file") List<MultipartFile> files) {
        var media = files.stream().map(file -> Media.builder()
                .mimeType(MimeTypeUtils.parseMimeType(file.getContentType()))
                .data(file.getResource())
                .build()).toArray(Media[]::new);
        return chatClient.prompt()
                .user(spec -> spec
                        .text("""
                                영수증 이미지에서 정보를 추출해 주세요.
                                - 날짜는 LocalDate, 시간은 LocalTime, 날짜시간은 LocalDateTime 형식으로 바꿔 주세요.
                                - 이미지에서 확인할 수 없는 값은 추측하지 말고 null로 반환하세요.
                                """)
                        .media(media))
                .call().entity(ReceiptOcrBatch.class);
    }
}
