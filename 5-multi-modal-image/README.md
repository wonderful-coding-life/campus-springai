# 이미지 멀티모달

## 멀티모달 입력 처리

멀티모달 입력을 사용하려면 사용자 메시지에 `Media` 객체를 포함하면 됩니다.

`Media` 객체는 다음 두 가지 정보로 구성됩니다.

* 미디어 파일을 나타내는 `Resource`
* 파일 형식을 나타내는 MIME 타입

Spring에서 제공하는 `ResourceLoader`를 사용하면 클래스패스, 파일 시스템, URL 등 다양한 위치에 있는 이미지, 오디오 등의 미디어 파일을 손쉽게 `Resource` 객체로 변환할 수 있습니다.

```java
Resource imageResource =
        resourceLoader.getResource("classpath:/images/receipt.png");

Media image = Media.builder()
        .mimeType(MimeTypeUtils.IMAGE_PNG)
        .data(imageResource)
        .build();

String response = chatClient.prompt()
        .user(user -> user
                .text("이미지의 내용을 분석해 주세요.")
                .media(image))
        .call()
        .content();
```

이처럼 텍스트와 미디어를 하나의 사용자 메시지에 함께 포함하여 AI 모델에 전달할 수 있습니다.

## 첨부 파일 크기 설정

이미지 업로드를 위해 `src/main/resources/application.properties`에 multipart 제한을 설정합니다.

```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
```

- `max-file-size`: 파일 1개의 최대 크기
- `max-request-size`: 한 요청에 포함되는 전체 multipart 데이터의 최대 크기

여러 이미지를 업로드하는 `/receipts` API는 전체 요청 크기가 `max-request-size`를 넘지 않아야 합니다.
