FROM gradle:jdk8 AS build-stage

WORKDIR /app

COPY build.gradle /app
COPY src /app/src

RUN gradle installDist


FROM openjdk:8-jre

WORKDIR /app

COPY --from=build-stage /app/build/install/app /app

CMD ["bin/app"]
