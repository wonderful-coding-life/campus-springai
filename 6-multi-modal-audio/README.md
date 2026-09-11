# 오디오 멀티모달

## 오디오 멀티모달 모델과 ChatClient 옵션 설정

일반 GPT 모델은 주로 텍스트와 이미지를 입력으로 처리합니다. 오디오 파일을 멀티모달 입력으로 전달하려면 `gpt-audio`와 같은 GPT Audio 계열 모델을 사용해야 합니다.

사용할 모델 등의 공통 옵션은 `ChatClient.Builder`의 `defaultOptions()`로 설정할 수 있습니다.

```java
@Bean
public ChatClient chatClient(ChatClient.Builder builder) {
    return builder
            .defaultOptions(ChatOptions.builder().model("gpt-audio"))
            .build();
}
```

`defaultOptions()`에 설정한 옵션은 모든 요청에 기본으로 적용됩니다. 다만 요청 시 `options()`를 호출하면 기본 옵션이 요청 옵션으로 대체되므로, 필요한 모델과 옵션을 요청 설정에도 포함해야 합니다.

```java
chatClient.prompt()
        .user("스프링 AI에 대해 짧게 설명해 주세요.")
        .options(OpenAiChatOptions.builder()
                .model("gpt-audio")
                .outputModalities(List.of("text", "audio")))
        .call();
```

`defaultUser()`와 `defaultSystem()`도 요청별 `user()`, `system()`을 호출하면 요청 내용으로 대체됩니다.

반면 `defaultAdvisors()`와 `defaultTools()`는 요청별 `advisors()`, `tools()`로 대체되지 않고 기존 설정에 추가되므로 중복 등록에 주의해야 합니다.

## 오디오 MIME 타입 정규화

브라우저는 MP3 파일을 `audio/mpeg`, WAV 파일을 `audio/wave`, `audio/x-wav` 등으로 전송할 수 있습니다.

그러나 Spring AI에서 OpenAI 오디오 입력을 사용할 때는 다음 MIME 타입으로 전달하는 것이 안전합니다.

```text
MP3 → audio/mp3
WAV → audio/wav
```

따라서 `MultipartFile#getContentType()` 값을 그대로 사용하지 말고 다음과 같이 정규화합니다.

```java
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
```

정규화한 MIME 타입은 `Media` 객체를 생성할 때 사용합니다.

```java
var media = Media.builder()
        .data(file.getResource())
        .mimeType(normalizeAudioMimeType(file.getContentType()))
        .build();
```
