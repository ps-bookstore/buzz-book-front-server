# 베이스 이미지 선택
FROM eclipse-temurin:21-jre

WORKDIR /app

ENTRYPOINT ["java", "-jar", "/target/bbfrontserver-0.0.1-SNAPSHOT.jar"]
