# 베이스 이미지 선택

FROM eclipse-temurin:21

WORKDIR /app

COPY /target/front-0.0.1-SNAPSHOT.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
