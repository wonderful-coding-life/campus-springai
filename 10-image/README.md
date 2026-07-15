# Image Generation

## Image Models
- 과거에는 DALL·E 2, DALL·E 3 모델이 사용되었으나, 이러한 DALL·E 계열 모델은 2026년 5월 12일 이후 더 이상 지원되지 않습니다.
- 대신 GPT Image models가 도입되어 이미지 생성 및 관련 작업을 수행할 수 있습니다.
- DALL·E 모델은 기본적으로 url 형식의 결과를 반환하며, 옵션을 통해 b64_json 형식으로도 응답을 받을 수 있습니다.
- 반면 GPT Image 모델은 별도의 옵션 없이 항상 b64_json 형식으로 이미지를 반환합니다.
- 일부 최신 GPT Image 모델은 보호(protected) 모델로 분류되어 있으며, 사용을 위해서는 계정에 대한 신분 인증 절차가 필요할 수 있습니다.
- 모델별로 지원하는 이미지 크기, 품질 등의 옵션이 상이하므로, 사용 시 각 모델의 공식 문서를 참고하는 것이 필요합니다.
- 생성된 각 이미지에는 Revised Prompt가 메타데이터로 포함되어 있어, 이미지 생성 과정에서 실제로 사용된 프롬프트를 확인할 수 있습니다.

## 프롬프트 작성
GPT Image 프롬프트는 "무엇을 그릴지"보다:
- 어떤 분위기인지
- 어떤 촬영 느낌인지
- 어떤 용도인지
- 어떤 스타일인지

를 구체적으로 적는 것이 중요하고, 한글도 잘 이해하지만, 현재는 영어 프롬프트가:

- 스타일 표현
- 광고 감성
- 카메라 연출
- 조명
- 질감

같은 부분에서 더 안정적이고 디테일하게 나오는 경우가 많음.

용도 명확, 브랜드 요구사항 구체적, 스타일 키워드 좋음, 조명과 분위기 표현 좋음.
```text
Create a premium Instagram marketing banner for Hacker's Cafe summer coffee promotion.

Requirements:
- prominently feature the brand name "Hacker's Cafe"
- modern premium cafe branding
- iced coffee on marble table
- warm natural sunlight
- clean luxury typography layout
- realistic commercial product photography
- stylish cafe advertisement design
- cinematic lighting
- Instagram 4:5 aspect ratio
- high-end lifestyle marketing style
```

장면 + 분위기 + 스타일 + 브랜드가 잘 설명
```text
화성 표면에서 탐사 로버가 움직이고 있으며, 그 옆에는 2족 보행 로봇이 함께 탐사 활동을 하고 있다.
탐사 로버와 2족 보행 로봇에는 모두 "Hacker's Campus" 로고와 브랜드명이 선명하게 표시되어 있다.
붉은 모래 언덕과 먼지 낀 하늘이 배경이며, 태양빛이 낮게 비추는 오후의 분위기.
실제 사진처럼 보이는 고해상도 장면, 자연스러운 그림자와 질감, 시네마틱한 우주 탐사 분위기, 사실적인 금속 재질 표현.
```

```text
A realistic Mars exploration scene.
A futuristic rover is moving across the Martian surface, while a humanoid biped robot is exploring beside it.
Both the rover and the humanoid robot prominently display the brand name and logo "Hacker's Campus".
Red sand dunes and a dusty Martian sky in the background, with low afternoon sunlight casting long natural shadows.
Ultra realistic photography style, cinematic space exploration atmosphere, detailed metallic textures, natural lighting, high-resolution image.

```

## Spring AI 2.0.0에서 OpenAI Java SDK 사용하기

Spring AI 2.0.0의 `ImageModel`은 텍스트 기반 이미지 생성 기능을 제공하지만, 기존 이미지를 수정하는 OpenAI Image Edit API는 지원하지 않습니다.

Image Edit 기능을 구현하려면 OpenAI 공식 Java SDK를 직접 사용해야 합니다.

### OpenAI Java SDK를 단독으로 사용하는 경우

OpenAI 공식 문서에서는 Java SDK를 사용할 때 다음 통합 모듈을 추가하도록 안내합니다.

```xml
<dependency>
    <groupId>com.openai</groupId>
    <artifactId>openai-java</artifactId>
    <version>4.0.0</version>
</dependency>
```

`openai-java`는 다음 모듈을 함께 제공하는 통합 라이브러리입니다.

* `openai-java-core`

    * `OpenAIClient` 인터페이스
    * API 요청 및 응답 모델
    * 이미지 생성 및 편집 관련 클래스
* `openai-java-client-okhttp`

    * OkHttp 기반 클라이언트 구현체
    * `OpenAIOkHttpClient` 클래스

따라서 `openai-java` 통합 모듈을 추가하면 다음 클래스를 모두 사용할 수 있습니다.

```java
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
```

### Spring AI 2.0.0과 함께 사용하는 경우

Spring AI 2.0.0의 OpenAI 연동 모듈인 `spring-ai-openai`는 이미 다음 OpenAI SDK 핵심 모듈을 포함하고 있습니다.

```text
com.openai:openai-java-core:4.39.1
```

따라서 다음 인터페이스와 OpenAI API 요청·응답 모델은 별도의 의존성 없이 사용할 수 있습니다.

```java
import com.openai.client.OpenAIClient;
```

하지만 `OpenAIOkHttpClient`는 `openai-java-core`가 아니라 별도의 `openai-java-client-okhttp` 모듈에 포함되어 있습니다.

```java
import com.openai.client.okhttp.OpenAIOkHttpClient;
```

Spring AI 2.0.0 프로젝트에서 `OpenAIOkHttpClient`를 사용하려면 Spring AI가 사용하는 OpenAI SDK 버전과 동일한 `4.39.1` 버전을 추가합니다.

#### Gradle

```gradle
implementation 'com.openai:openai-java-client-okhttp:4.39.1'
```

#### Maven

```xml
<dependency>
    <groupId>com.openai</groupId>
    <artifactId>openai-java-client-okhttp</artifactId>
    <version>4.39.1</version>
</dependency>
```

Maven Repository에서 버전과 의존성 정보를 확인할 수 있습니다.

https://mvnrepository.com/artifact/com.openai/openai-java-client-okhttp

### 클라이언트 생성 예시

```java
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

OpenAIClient openAIClient = OpenAIOkHttpClient.builder()
        .apiKey(openAiApiKey)
        .build();
```

환경 변수 `OPENAI_API_KEY`를 사용하는 경우에는 다음과 같이 생성할 수도 있습니다.

```java
OpenAIClient openAIClient = OpenAIOkHttpClient.fromEnv();
```

### 전체 `openai-java` 모듈을 추가하지 않는 이유

Spring AI 2.0.0 프로젝트에 다음과 같이 버전이 다른 통합 모듈을 추가하는 것은 권장하지 않습니다.

```xml
<dependency>
    <groupId>com.openai</groupId>
    <artifactId>openai-java</artifactId>
    <version>4.0.0</version>
</dependency>
```

Spring AI 2.0.0은 이미 `openai-java-core:4.39.1`을 사용하고 있습니다.

여기에 `openai-java:4.0.0`을 추가하면 서로 다른 버전의 OpenAI SDK 모듈이 함께 포함될 수 있으며, 의존성 해석 결과에 따라 다음과 같은 런타임 오류가 발생할 가능성이 있습니다.

```text
NoSuchMethodError
ClassNotFoundException
MethodNotFoundException
```

따라서 Spring AI 2.0.0과 함께 사용할 때는 `openai-java` 통합 모듈 전체를 추가하기보다, 필요한 클라이언트 구현 모듈만 동일한 버전으로 추가하는 것이 안전합니다.

```gradle
implementation 'com.openai:openai-java-client-okhttp:4.39.1'
```

### 정리

| 사용 환경                    | 추가할 의존성                                       |
| ------------------------ | --------------------------------------------- |
| OpenAI Java SDK 단독 사용    | `com.openai:openai-java`                      |
| Spring AI 2.0.0과 함께 사용   | `com.openai:openai-java-client-okhttp:4.39.1` |
| `OpenAIClient` 인터페이스만 사용 | Spring AI가 제공하는 `openai-java-core`로 사용 가능     |
| `OpenAIOkHttpClient` 사용  | `openai-java-client-okhttp` 추가 필요             |

결론적으로 Spring AI 2.0.0과 함께 OpenAI 공식 Java SDK의 OkHttp 클라이언트를 사용하려면 다음 의존성만 추가하면 됩니다.

```gradle
implementation 'com.openai:openai-java-client-okhttp:4.39.1'
```
