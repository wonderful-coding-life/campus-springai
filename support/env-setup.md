
# 환경변수 설정 방법 3가지

## IntelliJ 실행 설정에 반영

```
OPENAI_API_KEY=sk-proj-xxx;DB_URL=jdbc:mariadb://localhost:3306/mydb;DB_USER=myuser;DB_PASS=mypass
```

## 현재 명령 프롬프트에만 반영

윈도우 명령 프롬프트

```shell
set OPENAI_API_KEY=sk-proj-xxx
set DB_URL=jdbc:mariadb://localhost:3306/mydb
set DB_USER=myuser
set DB_PASS=mypass
```

## 영구적으로 사용자 환경변수 설정(현재 쉘에는 미반영)

윈도우 명령 프롬프트

```
setx OPENAI_API_KEY "sk-proj-xxx"
setx DB_URL "jdbc:mariadb://localhost:3306/mydb"
setx DB_USER "myuser"
setx DB_PASS "mypass"
```