FROM amazoncorretto:11-alpine-jdk
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8082
VOLUME /tmp
COPY target/shareit-gateway-0.0.1-SNAPSHOT.jar shareit-gateway.jar
ENTRYPOINT ["java", "-jar", "/shareit-gateway.jar"]