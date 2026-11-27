# GitHub Actions에서 빌드 · 테스트를 끝낸 jar(build/libs/app.jar)를 복사만 함
# RUN 단계가 없어서 ARM64 이미지도 에뮬레이션 없이 빠르게 만들어짐
FROM amazoncorretto:17-al2023-headless

WORKDIR /app

COPY build/libs/app.jar app.jar

# root가 아닌 사용자로 실행
USER 1000:1000

# 컨테이너 메모리의 75%까지 힙 사용
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]