FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Mavenをインストール
RUN apk add --no-cache maven
# ソースをコピー（初回ビルド用）
COPY . .
# Spring Bootを起動（DevToolsが有効なら自動リロードが走る）
CMD ["mvn", "spring-boot:run"]