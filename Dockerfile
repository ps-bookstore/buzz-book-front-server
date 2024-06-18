# 베이스 이미지 선택
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY app.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
