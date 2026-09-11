# Structured Output

## ChatClient의 `entity()`를 이용한 객체 변환

Spring AI의 `ChatClient`에서 `entity()`를 사용하면 AI의 응답을 Java 객체로 변환할 수 있습니다.

### 단일 객체 변환

단일 객체를 반환받을 때는 변환할 클래스 타입을 전달합니다.

```java
Receipt receipt = chatClient.prompt()
        // ...
        .entity(Receipt.class);
```

`entity(Receipt.class)`는 내부적으로 `BeanOutputConverter<Receipt>`를 사용하여 다음 작업을 수행합니다.

1. `Receipt` 클래스 구조를 분석합니다.
2. AI가 반환할 JSON 형식을 구성합니다.
3. AI 응답을 JSON 형태로 받습니다.
4. JSON을 `Receipt` 객체로 변환합니다.

### 리스트 타입 변환

`List<Receipt>`처럼 제네릭 타입을 반환받을 때는 `ParameterizedTypeReference`를 사용합니다.

```java
List<Receipt> receipts = chatClient.prompt()
        // ...
        .entity(new ParameterizedTypeReference<List<Receipt>>() {});
```

자바에서는 타입 소거로 인해 `List.class`만으로 리스트 내부의 객체 타입을 알 수 없습니다.

따라서 구체적인 제네릭 타입을 전달해야 합니다.

## 날짜, 시간 처리

AI가 Java의 `LocalDateTime` 형식을 충분히 인식하므로 프롬프트에는 “날짜와 시간은 LocalDateTime 형식으로 추출해 주세요.”라고 간단히 작성해도 되며, 이는 AI 응답을 `LocalDateTime` 필드로 변환할 때 날짜·시간 형식 불일치로 인한 역직렬화 오류를 줄이기 위해 필요합니다.

