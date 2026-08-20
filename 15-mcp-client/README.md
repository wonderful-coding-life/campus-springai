# MCP Client

지금까지 작성한 MCP Server와 연결하여 챗봇 기능을 제공하는 프로젝트입니다.

## 프로젝트 셋업

### 스프링 이니셜라이저

- Spring Web
- Spring Security
- JDBC Chat Memory Repository
- MariaDB Vector Database
- MariaDB Driver
- Model Context Protocol Client
- OpenAI
- Lombok

### 애플리케이션 설정 (application.properties)

Streamable MCP Server 등록을 위해 다음과 같이 설정합니다.
`xxx`부분은 등록할 MCP Server 이름으로 대체합니다.

```properties
# Streamable HTTP 방식 MCP 서버 주소
spring.ai.mcp.client.streamable-http.connections.xxx.url=xxx
```

STDIO MCP Server 등록을 위해 다음과 같이 설정합니다.
`xxx`부분은 등록할 MCP Server 이름으로 대체합니다.

```properties
# STDIO 방식 MCP 서버 실행 명령어
spring.ai.mcp.client.stdio.connections.xxx.command=xxx
# 실행 시 전달할 명령행 인수
spring.ai.mcp.client.stdio.connections.xxx.args=xxx
# MCP 서버에 전달할 환경 변수
spring.ai.mcp.client.stdio.connections.xxx.env.xxx=${xxx}
```

## MCP Client

application.properties에 MCP 서버 연결을 설정하고 spring-ai-starter-mcp-client를 포함하면, Spring AI가 각 MCP 서버의 도구를 모아 ToolCallbackProvider 빈으로 자동 구성합니다.

Spring AI 2.0.0에서는 이 Provider를 ChatClient의 .defaultTools(...) 또는 .tools(...)에 전달하면 됩니다.

## Notion MCP 서버 연결 설정

Notion MCP 서버가 사용자의 Notion 워크스페이스에 페이지를 생성하거나 수정하려면 Notion Integration을 만들고, 작업할 페이지에 해당 Integration을 연결해야 합니다.

### 1. Notion 워크스페이스 준비

Notion에서 MCP 서버가 작업할 워크스페이스와 페이지를 생성합니다.

### 2. Notion Integration 생성

1. [Notion 개발자 포털](https://app.notion.com/developers)에 접속합니다.
2. `연결` 메뉴에서 `신규 연결`을 선택합니다.
3. 다음 정보를 입력합니다.

    * 연결 이름
    * 설치할 워크스페이스

4. 연결을 생성합니다.
5. 생성된 Integration의 액세스 토큰을 복사하여 애플리케이션에 등록합니다.

### 3. 작업할 페이지에 Integration 연결

1. MCP 서버가 작업할 Notion 페이지를 엽니다.
2. 페이지 오른쪽 위의 `···` 메뉴를 선택합니다.
3. `연결` 메뉴에서 앞에서 생성한 Integration을 선택합니다.

Integration이 페이지에 연결되어 있어야 MCP 서버가 해당 페이지에 접근할 수 있습니다.

### 4. 페이지 ID 확인

작업할 페이지에서 `링크 복사`를 선택합니다.

복사된 링크는 다음과 같은 형태입니다.

```text
https://app.notion.com/p/{page-id}?source=copy_link
https://app.notion.com/p/AI-{page-id}?source=copy_link
```

노션 페이지 URL의 구조는 조금씩 바뀌고 있으나 마지막 32자리의 UUID 부분 `{page-id}` 이 부분이 페이지 ID이고 이것을 MCP 서버 설정에 사용합니다.

### 5. MCP Inspector로 테스트

MCP Inspector를 실행합니다.

```
npx @modelcontextprotocol/inspector
```

다음과 같이 서버를 등록합니다.

* Transport - stdio (local process)
* Command - npx
* Arguments - 두줄에 나누어 각각 -y 그리고 @notionhq/notion-mcp-server
* Environment - NOTION_TOKEN=ntn_xxx

페이지 작성은 API-post-page 도구를 선택하고 다음과 같이 입력

* parent

```json
{
  "page_id": "3c2597ed70478092a2bbf781dc7afbbd"
}
```

* properties

```json
{
  "title": {
    "title": [
      {
        "type": "text",
        "text": {
          "content": "MCP 테스트 페이지"
        }
      }
    ]
  }
}
```

* children

```json
[
  {
    "object": "block",
    "type": "paragraph",
    "paragraph": {
      "rich_text": [
        {
          "type": "text",
          "text": {
            "content": "Notion MCP Inspector에서 생성한 테스트 페이지입니다."
          }
        }
      ]
    }
  }
]
```
