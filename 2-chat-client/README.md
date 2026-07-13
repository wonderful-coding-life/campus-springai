# 2. ChatClient

## ChatModel vs. ChatClient

* **ChatModel**

    * 대화형 AI 모델과 연동하기 위한 추상화 인터페이스이다.
    * 모델에 메시지를 전달하고 응답을 받는 기본 기능을 제공한다.

* **ChatClient**

    * `ChatModel`을 보다 편리하게 사용할 수 있도록 지원하는 고수준 API이다.
    * Fluent API 스타일로 프롬프트를 구성할 수 있다.
    * Advisor를 적용하여 대화 메모리, RAG, 로깅 등의 부가 기능을 확장할 수 있다.
    * Spring AI에서는 일반적으로 `ChatModel`을 직접 호출하기보다 `ChatClient` 사용을 권장한다.

## SystemMessage vs. UserMessage

* **SystemMessage**

    * AI의 역할, 답변 방식, 출력 형식과 같은 고정된 동작 규칙을 전달하는 메시지이다.
    * 예: “너는 전문 마케팅 카피라이터야.”

* **UserMessage**

    * 사용자의 실제 질문이나 요청 내용을 전달하는 메시지이다.
    * 요청할 때마다 입력 내용이 달라질 수 있다.
    * 예: 제품명, 가격, 구매 링크, 제품 특징

## Markdown

* Markdown은 HTML에 비해 문법이 단순하고, 원문 자체의 가독성도 높다.
* 대화형 AI 모델은 제목, 목록, 강조 표현 등을 포함한 답변을 Markdown 형식으로 생성하는 경우가 많다.
* `org.commonmark:commonmark` 라이브러리를 사용하면 Markdown 형식의 문자열을 HTML로 변환할 수 있다.

## Demo

* **REST API**

    * `POST /chats`

* **Spring MVC**

    * `/marketing`

### 입력 예시

```text
제품명: 캠퍼스 썬크림
가격: 10,000원
구매 링크: http://campus.com/p123
제품 특징:
- 자외선 차단 효과가 뛰어나다.
- 자극이 적어 민감한 피부에도 적합하다.
- 끈적임이 적다.
- 대용량 제품으로 온 가족이 함께 사용할 수 있다.
- 현재 1+1 행사를 진행 중이다.
```
