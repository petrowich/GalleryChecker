FROM openjdk:11-jre-slim
ARG JAR_FILE
COPY target/GalleryChecker.jar /usr/src/app/GalleryChecker.jar
ENTRYPOINT ["java","-jar","/usr/src/app/GalleryChecker.jar"]