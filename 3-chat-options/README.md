
# ChatOptions

## RateLimit 테스트 참고

Spring AI `2.0.0` 정식 버전에서는 `RateLimit` 메타데이터가 `EmptyRateLimit`으로 반환되는 문제가 있다. `2.0.0-M4` 버전에서는 정상적으로 동작하므로, 이전 동작을 확인하려면 `build.gradle`에 다음과 같이 버전을 설정한다.

```groovy
ext {
    set('springAiVersion', '2.0.0-M4')
}
```

OpenAI API 키는 애플리케이션 설정에 다음과 같이 등록한다.

```properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
```

`OPENAI_API_KEY` 환경 변수를 설정한 후 애플리케이션을 실행하면 `ChatResponseMetadata`에서 `RateLimit` 정보를 확인할 수 있다.

---

## Temperature와 Top P

대화형 AI 모델은 `temperature`와 `topP` 옵션을 사용하여 답변의 다양성을 조절할 수 있다.

### Temperature

`temperature`는 답변의 무작위성과 창의성을 조절하는 옵션이다.

* 값이 낮을수록 일관되고 예측 가능한 답변을 생성한다.
* 값이 높을수록 다양하고 창의적인 답변을 생성한다.

### Top P

`topP`는 다음 토큰을 선택할 때 고려할 후보의 범위를 조절하는 옵션이다.

* 값이 낮을수록 가능성이 높은 일부 후보만 고려한다.
* 값이 높을수록 더 다양한 후보를 고려한다.

일반적으로 `temperature`와 `topP`를 동시에 크게 변경하기보다는 하나의 옵션을 중심으로 조절하는 것이 좋다.

### 실행 예제

```java
var completion = chatClient.prompt()
        .user("커피를 홍보하는 짧은 문구를 작성해 주세요.")
        .options(ChatOptions.builder()
                .model("gpt-4o")
                .temperature(0.8)
                .topP(0.3))
        .call()
        .content();
```

### 실행 결과

#### `temperature`를 `0.8`, `topP`를 `0.3`

```text
한 모금의 여유, 커피로 시작하세요!
```

#### `temperature`를 `1.2`, `topP`를 `1.0`

```text
매일의 재충전, 한 잔의 완벽한 순간 - 커피 한 잔 하실래요?
```

