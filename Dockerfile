FROM amazoncorretto:11-alpine-jdk
MAINTAINER https://github.com/joseluisgs/Kotlin-SpringBoot-REST-Service
COPY target/SpringDAM-0.0.1-SNAPSHOT.jar SpringBootDAM.jar
ENTRYPOINT ["java","-jar","/SpringBootDAM.jar"]