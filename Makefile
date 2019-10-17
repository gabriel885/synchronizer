build:
	@docker build -t synchronizer:latest .

run: run-client-1 run-client-2 run-client-3 run-client-4

run-client-1:
	@docker run -it --rm synchronizer:latest

run-client-2:
	@docker run -it --rm synchronizer:latest

run-client-3:
	@docker run -it --rm synchronizer:latest

run-client-4:
	@docker run -it --rm synchronizer:latest

run-shell:
	@docker run -it --rm synchronizer:latest /bin/bash

kill:
	docker stop $(docker ps -q --filter ancestor=<synchronizer:latest> )


.PHONY: run run-client-1 run-client-2 run-client-3 run-client-4 build