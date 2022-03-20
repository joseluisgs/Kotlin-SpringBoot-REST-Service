FROM amazoncorretto:11-alpine-jdk
MAINTAINER https://github.com/joseluisgs/Kotlin-SpringBoot-REST-Service
COPY build/libs/Kotlin-SpringBoot-REST-Service-0.0.1-SNAPSHOT.jar KotlinSpringBoot.jar
ENTRYPOINT ["java","-jar","/KotlinSpringBoot.jar"]