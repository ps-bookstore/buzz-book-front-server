# 베이스 이미지 선택
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY /target/bbfrontserver-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

