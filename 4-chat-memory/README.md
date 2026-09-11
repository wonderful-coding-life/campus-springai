# Chat Memory

## 프로젝트 셋업

### 스프링 이니셜라이저

- Spring Web
- JDBC Chat Memory Repository
- MariaDB Driver
- OpenAI
- Lombok

### 애플리케이션 설정 (application.properties)

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

spring.ai.chat.memory.repository.jdbc.initialize-schema=always
```
