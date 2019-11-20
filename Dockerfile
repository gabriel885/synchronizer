FROM openjdk:8


# create directory for java application
RUN mkdir -p /java/synchronizer
# copy fat jar to container
COPY target/synchronizer-jar-with-dependencies.jar /java/synchronizer
WORKDIR /java/synchronizer

# default monitorable files
RUN mkdir -p /opt/dir
RUN echo "monitorable file" > /opt/dir/example.txt

# intstall vim to edit files later inside the containers
# RUN ["apt-get", "update"]
# RUN ["apt-get", "install", "-y", "vim"]

CMD ["java", "-jar", "synchronizer-jar-with-dependencies.jar", "-p", "/opt/dir", "-d" ,"172.18.0.10:2020","172.18.0.15:2020"]
