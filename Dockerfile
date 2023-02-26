FROM openjdk:17

COPY http/build/libs/http.jar /http.jar

CMD [ "java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "/http.jar" ]
