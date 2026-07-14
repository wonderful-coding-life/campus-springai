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

## PagePdfDocumentReader

PDF 문서를 페이지 단위로 읽어 `Document` 객체로 변환합니다.

### 장점

* 일반적인 PDF 문서를 쉽게 처리할 수 있습니다.
* 페이지 수를 기준으로 `Document` 크기를 조절할 수 있습니다.
* 목차가 없는 PDF에도 사용할 수 있습니다.

### 단점

* 제목이나 문단 구조를 정확하게 반영하기 어렵습니다.
* 표, 이미지, 복잡한 레이아웃은 텍스트 추출 결과가 불안정할 수 있습니다.
* 내용이 페이지 중간에서 나뉠 수 있습니다.

## ParagraphPdfDocumentReader

PDF의 목차와 문서 계층 정보를 기준으로 내용을 나누어 `Document` 객체로 변환합니다.

### 장점

* 문서의 목차와 장·절 구조를 활용할 수 있습니다.
* 페이지 단위보다 의미 있는 내용 단위로 분리하기 좋습니다.
* 구조가 잘 작성된 매뉴얼이나 보고서에 적합합니다.

### 단점

* PDF에 목차나 카탈로그 정보가 있어야 제대로 동작합니다.
* 스캔 PDF나 구조 정보가 없는 PDF에는 적합하지 않습니다.
* PDF의 목차 구조가 부정확하면 분리 결과도 부정확할 수 있습니다.

## MarkdownDocumentReader

Markdown 문서를 제목과 문단 구조에 따라 읽어 `Document` 객체로 변환합니다.

### 장점

* 제목 구조를 기준으로 문서를 나누기 좋습니다.
* 코드 블록, 인용문, 수평선 등을 설정할 수 있습니다.
* 여러 Markdown 파일을 경로 패턴으로 한 번에 읽을 수 있습니다.

### 단점

* Markdown 형식으로 작성된 문서만 처리할 수 있습니다.
* 상위 제목 정보가 하위 문서에 자동으로 유지되지 않습니다.
* 특정 제목 단계만 분리 기준으로 지정하는 기능은 제한적입니다.

## Vector Store 메타데이터 조회 및 삭제

MariaDB의 `vector_store` 테이블에서 `metadata.category` 값이 `pdf`인 데이터를 조회하거나 삭제하는 SQL입니다.

### 조회

삭제하기 전에 대상 데이터를 먼저 확인합니다.

```sql
SELECT *
FROM vector_store
WHERE JSON_VALUE(metadata, '$.category') = 'pdf';
```

### 삭제

`metadata.category` 값이 `pdf`인 데이터를 삭제합니다.

```sql
DELETE FROM vector_store
WHERE JSON_VALUE(metadata, '$.category') = 'pdf';
```

> 삭제한 데이터는 복구하기 어려우므로, 반드시 `SELECT` 문으로 대상 데이터를 확인한 후 실행하세요.
