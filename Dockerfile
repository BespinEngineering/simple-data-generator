# Use the official OpenJDK 17 base image
FROM amazoncorretto:17.0.8

# Set the working directory in the docker image
WORKDIR /app

# Copy the JAR file and the configuration file into the image
COPY build/libs/simple-data-generator-3.0.0-SNAPSHOT-fatJar.jar /app
COPY java.policy /app
COPY build_docker_keystore.bash /app
COPY docker_run.bash /app

RUN yum install -y openssl

# Run the JAR file with the java command and provide the configuration file as an argument
CMD ["/app/docker_run.bash"]

