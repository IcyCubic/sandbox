FROM java:8 

# Install maven
RUN apt-get update && apt-get install -y maven

WORKDIR /code

# Download dependencies
ADD pom.xml /code/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

# Add source, compile and package into a jar
ADD src /code/src
ADD test /code/test
RUN ["mvn", "package"]

ENTRYPOINT ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar", "target/signature_tool-0.0.1-SNAPSHOT.jar"]