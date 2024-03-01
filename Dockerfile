# Use the official OpenJDK 17 base image
FROM alpine:3.18

# Update the package manager and install OpenSSL
RUN apk update && apk add zsh && apk add openssl

# Install the latest version of OpenJDK (JRE)
RUN apk add openjdk17 && apk update

# Setup container to run as non-root user
RUN adduser -D -g "Simple Data Generator ID" sdg sdg
USER sdg

# Set environment variables (optional)
ENV JAVA_HOME=/usr/lib/jvm/default-jvm
ENV PATH=$PATH:$JAVA_HOME/bin

# Set the working directory in the docker image
WORKDIR /app

# Copy the JAR file and the configuration file into the image
COPY build/libs/simple-data-generator-*-fatJar.jar /app
COPY java.policy /app
COPY build_docker_keystore.zsh /app
COPY docker_run.zsh /app

# Run the JAR file with the java command and provide the configuration file as an argument
CMD ["/app/docker_run.bash"]
