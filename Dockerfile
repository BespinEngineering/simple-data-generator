# Use the official OpenJDK 17 base image
FROM alpine:3.18

# Update the package manager and install OpenSSL
RUN apk update && apk add bash && apk add openssl

# Install the latest version of OpenJDK (JRE)
RUN apk add openjdk17 && apk update

# Set environment variables (optional)
ENV JAVA_HOME=/usr/lib/jvm/default-jvm
ENV PATH=$PATH:$JAVA_HOME/bin

# Set the working directory in the docker image
WORKDIR /app

# Copy the JAR file and the configuration file into the image
COPY build/libs/simple-data-generator-3.0.0-SNAPSHOT-fatJar.jar /app
COPY java.policy /app
COPY build_docker_keystore.bash /app
COPY docker_run.bash /app

# Run the JAR file with the java command and provide the configuration file as an argument
CMD ["/app/docker_run.bash"]
