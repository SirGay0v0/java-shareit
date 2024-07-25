FROM amazoncorretto:11-alpine-jdk
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8083
VOLUME /tmp
COPY target/shareit-server-0.0.1-SNAPSHOT.jar shareit-server.jar
ENTRYPOINT ["java","-jar","/shareit-server.jar"]
