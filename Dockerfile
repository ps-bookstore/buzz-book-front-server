# 베이스 이미지 선택
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY /home/runner/work/buzz-book-front-server/buzz-book-front-server/target/bbfrontserver-0.0.1-SNAPSHOT.jar /app/bbfrontserver-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
