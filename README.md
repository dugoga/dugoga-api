# 🍔 DUGOGA
## 팀원
| <img src="https://github.com/oni128.png" width="110"> | <img src="https://avatars.githubusercontent.com/u/120404242?v=4" width="110"> | <img src="https://github.com/soo96.png" width="110"> | <img src="https://github.com/jsh9057.png" width="110"> | <img src="https://github.com/hjk2132.png" width="110"> |
| :-----------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------: |
| 이예원 | 김민진 | 이현수 | 정승현 | 홍준기 |
| 카테고리<br>서비스 지역<br>즐겨찾기 | 인증 / 인가<br>회원 | 주문<br>결제 | 음식점<br>상품 | 리뷰<br>AI |
| [<img src="https://img.shields.io/badge/Github-Link-181717?logo=Github">](https://github.com/oni128) | [<img src="https://img.shields.io/badge/Github-Link-181717?logo=Github">](https://github.com/gsemily) | [<img src="https://img.shields.io/badge/Github-Link-181717?logo=Github">](https://github.com/soo96) | [<img src="https://img.shields.io/badge/Github-Link-181717?logo=Github">](https://github.com/jsh9057) | [<img src="https://img.shields.io/badge/Github-Link-181717?logo=Github">](https://github.com/hjk2132) |
<br>

# ✨ 프로젝트 소개

### 광화문 근처에서 운영될 음식점들의 배달 주문 관리, 결제, 그리고 주문 내역 관리 기능을 제공하는 플랫폼입니다.

- 고객, 가게 주인, 관리자 간의 주문, 주문 처리, 결제를 하나의 시스템으로 통합하여 관리합니다.
- 고객은 온라인을 통해서만 주문할 수 있으며, 가게 주인은 주문을 처리하고 상태를 변경할 수 있습니다.
- AI를 활용하여 가게 사장님이 상품 설명을 쉽게 작성할 수 있도록 지원합니다.
- 향후 확장성을 고려해 운영 지역, 음식점 카테고리는 별도로 관리합니다.
<br>

# 📱 서비스 구성 및 실행 방법

## 서비스 구성

DUGOGA는 Spring Boot 기반 REST API 서버로 구성된 배달 서비스 백엔드 프로젝트입니다.

- Spring Boot 기반 REST API 서버
- PostgreSQL 데이터베이스 사용
- Redis 캐시 적용
- AWS S3 기반 이미지 파일 저장
- Docker Compose 기반 컨테이너 환경 구성
- GitHub Actions + AWS EC2 기반 CI/CD 배포

| 분류         | 상세                                                     |
| ------------ |----------------------------------------------------------|
| IDE          | IntelliJ IDEA                                            |
| Language     | Java 21                                                  |
| Framework    | Spring Boot 3.5.11                                       |
| Build Tool   | Gradle                                                   |
| DevOps       | AWS EC2, AWS S3, Docker, Docker Compose, GitHub Actions  |
| DB           | PostgreSQL, H2 (Test), Redis                             |
| Security     | Spring Security, JWT (jjwt 0.12.5)                       |
| Cache        | Redis                                                    |
| Testing      | JUnit 5, Spring Boot Test, Testcontainers                |
| Documentation| Swagger                                                  |
| AI           | Spring AI, OpenAI API                                    |

## 실행 방법

### 1. 프로젝트 클론

```bash
git clone https://github.com/dugoga/dugoga-api.git
cd dugoga-api
```
### 2. 환경 설정

**`application.yml` 또는 `.env` 파일에 아래 환경 변수를 설정하세요.**

```properties
# 🗄️ Database (PostgreSQL)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/dugoga
DB_USERNAME=<POSTGRES_USER>
DB_PASSWORD=<POSTGRES_PASSWORD>

# 🧠 Redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379

# 🔐 JWT
JWT_SECRET_KEY=<YOUR_JWT_SECRET_KEY>
JWT_ACCESS_TOKEN_EXPIRATION=3600000
JWT_REFRESH_TOKEN_EXPIRATION=1209600000

# ☁️ AWS S3
AWS_ACCESS_KEY=<YOUR_AWS_ACCESS_KEY>
AWS_SECRET_KEY=<YOUR_AWS_SECRET_KEY>
AWS_REGION=ap-northeast-2
AWS_S3_BUCKET=<YOUR_BUCKET_NAME>
AWS_S3_PRESIGNED_EXPIRATION=3600

# 🤖 OpenAI
OPENAI_API_KEY=<YOUR_OPENAI_API_KEY>
```

### 3. Docker로 PostgreSQL · Redis 실행
```bash
docker compose up -d
```
### 4. 애플리케이션 실행

```bash
./gradlew bootRun
```

## 📚 문서

### ERD
<details>
    <summary>ERD 상세보기</summary>
<img width="2321" height="1524" alt="Dugoga-ERD" src="https://github.com/user-attachments/assets/e17ad569-9a79-423a-b801-74d032799070" />
</details>3

### 시스템 아키텍처
<details>
    <summary>시스템 아키텍처 상세보기</summary>

</details>


### API docs

서비스를 실행한 후 아래 주소로 접속하시면 Swagger 화면을 확인할 수 있습니다.
```text
http://localhost:8080/swagger-ui/index.html
```

<br><br>

## 📁 폴더 구조

```text
📂 src/main
 ┣ 📂 java/com/project/dugoga
 ┃ ┣ 📄 DugogaApplication.java
 ┃ ┣ 📂 domain
 ┃ ┃ ┣ 📂 aiPrompt
 ┃ ┃ ┣ 📂 availableaddress
 ┃ ┃ ┣ 📂 bookmark
 ┃ ┃ ┣ 📂 category
 ┃ ┃ ┣ 📂 image
 ┃ ┃ ┣ 📂 order
 ┃ ┃ ┣ 📂 payment
 ┃ ┃ ┣ 📂 product
 ┃ ┃ ┣ 📂 review
 ┃ ┃ ┣ 📂 store
 ┃ ┃ ┗ 📂 user
 ┃ ┗ 📂 global
 ┃   ┣ 📂 config
 ┃   ┣ 📂 dto
 ┃   ┣ 📂 entity
 ┃   ┣ 📂 exception
 ┃   ┣ 📂 filter
 ┃   ┣ 📂 infrastructure
 ┃   ┗ 📂 security
 ┗ 📂 resources
```
Domain 중심 패키지 구조를 사용하여 기능을 도메인 단위로 분리하고,
global 패키지에서 공통 설정, 보안, 예외 처리를 관리하도록 구성했습니다.

## 🛠️ 기술 스택
<div align="left">

<h3>Backend</h3>
<div dir="auto">
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"/>
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">

![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![Spring JPA](https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)

<h3>DB</h3>
<img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white">
<img src="https://img.shields.io/badge/Redis-FF4438?style=for-the-badge&logo=redis&logoColor=white">

</div>

<h3>Infra</h3>
<img src = "https://img.shields.io/badge/GITHUB%20ACTIONS-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"/>
<img src = "https://img.shields.io/badge/AWS-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white"/>

<h3>Tools & Communication</h3>
<div dir="auto">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
<img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white"/>
<img src="https://img.shields.io/badge/ERDCLOUD-339AF0?style=for-the-badge&logoColor=white">
<img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white">
<img src="https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white"/>
</div>



