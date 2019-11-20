# build maven project
build:
	@mvn package

# build java project and build docker image
build-all: clean build build-docker

# clean java targets (jar and .class files)
clean:
	@mvn clean

# run java target with arguments
run:
	@java -jar target/synchronizer-jar-with-dependencies.jar -p /Users/gabrielmunits/opt/dir -d 172.18.0.10:2020 172.18.0.15:2020

create-network:
	@docker network create --subnet=172.18.0.0/16 mynet123

# build docker image
build-docker:
	@docker build . -t synchronizer:latest

# run all docker clients
run-all: run-docker-client-1 run-docker-client-2

# run docker client 1
run-docker-client-1:
	@docker run --rm --net mynet123 --ip 172.18.0.10 -it  synchronizer:latest

run-docker-client-2:
	@docker run --rm --net mynet123 --ip 172.18.0.15 -it synchronizer:latest

# kill all docker clients
kill:
	docker stop $(docker ps -q --filter ancestor=<synchronizer:latest> )

