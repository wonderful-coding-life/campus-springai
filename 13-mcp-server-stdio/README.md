# MCP Server (Stdio)

현재 날짜와 시간을 알려주는 MCP Server를 작성합니다.

## 프로젝트 셋업
 
### 스프링 이니셜라이저

- Model Context Protocol Server
- Lombok

### 애플리케이션 설정 (application.properties)

STDIO 방식의 경우 표준 입출력을 사용하므로 애플리케이션에서 콘솔로의 입출력을 하면 안됩니다.
따라서 다음과 같이 로그 출력을 모두 끄고 필요하다면 파일로 출력하도록 합니다.

```properties
spring.ai.mcp.server.stdio=true
logging.pattern.console=
spring.main.banner-mode=off
spring.main.log-startup-info=false
logging.file.name=./log/datetime-mcp-server.log
```

## MCP Server 테스트

- bootJar로 빌드하고 빌드된 jar 파일을 C:\tools\local-tools-mcp-server.jar 파일로 복사
- npx @modelcontextprotocol/inspector를 실행하고 다음과 같이 입력
    - Transport Type: STDIO
    - Command: java
    - Arguments: -jar C:/tools/local-tools-mcp-server.jar