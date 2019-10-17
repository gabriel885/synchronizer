FROM openjdk:8

# create directory for java application "synchronizer"
RUN mkdir -p /java/synchronizer
COPY target /java/synchronizer
WORKDIR /java/synchronizer/classes/synchronizer

# put default monitorable files
RUN mkdir -p /opt/dir
RUN echo "monitorable file" > /opt/dir/example.txt

CMD ["java", "-cp", "main.java.synchronizer.synchronizer.Main", "-p", "/opt/dir"]