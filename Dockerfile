## BUILD STAGE ##
FROM gradle:8.12.0-jdk21-alpine AS build

COPY . /tmp/app
WORKDIR /tmp/app

# following nixpacks build command https://nixpacks.com/docs/providers/java
RUN --mount=type=secret,id=GITHUB_USERNAME,env=GITHUB_USERNAME,required=true \
    --mount=type=secret,id=GITHUB_TOKEN,env=GITHUB_TOKEN,required=true \
    gradle clean build -x test -x check

RUN mkdir -p /tmp/extracted && java -Djarmode=layertools -jar $(ls -1 /tmp/app/build/libs/*jar | grep -v plain) extract --destination /tmp/extracted

## DISTROLESS IMAGE ##
FROM gcr.io/distroless/java21-debian12:nonroot
WORKDIR /tmp/app

COPY --from=build /tmp/extracted/dependencies /tmp/app/
COPY --from=build /tmp/extracted/spring-boot-loader /tmp/app/
COPY --from=build /tmp/extracted/snapshot-dependencies /tmp/app/
COPY --from=build /tmp/extracted/application /tmp/app/

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
