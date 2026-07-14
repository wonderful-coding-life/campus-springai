# RAG (Retrieval-Augmented Generation) Project

## JDBC 의존성 구성

JPA 또는 MyBatis의 Spring Boot Starter를 사용하면 내부적으로 `spring-boot-starter-jdbc`가 포함되므로 별도로 추가할 필요가 없습니다.

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
```

또는

```gradle
implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter'
```

반면 MariaDB JDBC 드라이버만 추가한 경우에는 Spring의 JDBC 자동 설정이 포함되지 않습니다.

```gradle
runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
```

JPA나 MyBatis 없이 MariaDB Vector Store를 사용하려면 다음 의존성을 함께 추가해야 합니다.

```gradle
implementation 'org.springframework.boot:spring-boot-starter-jdbc'
implementation 'org.springframework.ai:spring-ai-starter-vector-store-mariadb'
runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
```

* `mariadb-java-client`: MariaDB JDBC 드라이버
* `spring-boot-starter-jdbc`: `DataSource`, `JdbcTemplate`, 트랜잭션 및 JDBC 자동 설정
* `spring-ai-starter-vector-store-mariadb`: MariaDB Vector Store 구현

