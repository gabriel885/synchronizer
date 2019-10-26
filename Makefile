build:
	@mvn clean package

run:
	@java -jar target/synchronizer-jar-with-dependencies.jar -p /Users/gabrielmunits/opt/dir

build-docker:
	@docker build . -t synchronizer:lates

run-all: run-client-1 run-client-2 run-client-3 run-client-4

run-client-1:
	@docker run -it --rm synchronizer:latest

run-client-2:
	@docker run -it --rm synchronizer:latest

run-client-3:
	@docker run -it --rm synchronizer:latest

run-client-4:
	@docker run -it --rm synchronizer:latest

kill:
	docker stop $(docker ps -q --filter ancestor=<synchronizer:latest> )
