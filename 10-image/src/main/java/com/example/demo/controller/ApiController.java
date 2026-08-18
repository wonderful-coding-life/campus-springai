package com.example.demo.controller;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Objects;

@RestController
public class ApiController {
    @Autowired
    private ImageModel imageModel;

    @PostMapping(value = "/images", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImages(@RequestBody String message) {
        var options = OpenAiImageOptions.builder()
                .model("gpt-image-2")
                .build();
        var response = imageModel.call(new ImagePrompt(message, options));
        var b64Json = response.getResult().getOutput().getB64Json();
        return Base64.getDecoder().decode(b64Json);
    }
}
