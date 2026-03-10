# 환경 설정
FROM eclipse-temurin:21-jdk-jammy

# jar 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 실행명령 -> 컨테이너 시작 시 실행
ENTRYPOINT ["java","-jar","/app.jar"]




