# 베이스 이미지 선택
FROM eclipse-temurin:21

# JAR 파일을 컨테이너의 파일 시스템으로 복사
COPY app.jar /app.jar

# JAR 파일을 실행하도록 설정
ENTRYPOINT ["java", "-jar", "/app.jar"]
