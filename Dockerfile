FROM maven:3.9.9-eclipse-temurin-17

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        libasound2t64 \
        libgl1 \
        libgtk-3-0 \
        libxext6 \
        libxi6 \
        libxrender1 \
        libxtst6 \
        netcat-openbsd \
        xauth \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY . .
RUN sed -i 's/\r$//' docker/app/entrypoint.sh \
    && chmod +x docker/app/entrypoint.sh

ENTRYPOINT ["docker/app/entrypoint.sh"]
CMD ["mvn", "javafx:run"]
