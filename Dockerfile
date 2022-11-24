############################################
# First stage: complete build environment
# Can also be used as a development environment
FROM gradle:7.5.1-jdk8 as builder
COPY . /usr/src/opencraft
WORKDIR /usr/src/opencraft
RUN ./gradlew clean build --no-build-cache --parallel --no-daemon

############################################
# Second stage: minimal runtime environment
FROM amazoncorretto:8
# Copy jar from the first stage
COPY --from=builder /usr/src/opencraft/build/libs/*-all.jar opencraft.jar
CMD ["java", "-jar", "opencraft.jar"]
