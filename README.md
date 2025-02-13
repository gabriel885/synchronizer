![assignment](docs/assignment.png)
[![DEMO](docs/demo_thumbnail.png)](https://vimeo.com/385016876)
Click the image to view the demo or click [HERE](https://vimeo.com/385016876)

## Objective
Java program called "synchonizer" responsible for synchronizing path file data
between multiple computers.

## Prerequesites
 * JDK 1.8
 * Maven
 * Docker (optional)

### Principles
 * EventBus Actions
    * outcoming.actions
    * incoming.actions
 * SharedData local maps
    * global.path
 * Actions
    * Create - create action
    * Delete - delete action
    * Modify - modification action
    * Ack - acknowledgment
    * Nack - negative acknowledgement
    * Request - request a file
    * Response - response with requested file
### Design/Functionality
 * Multi Threaded Server Application
    * Storage Application
        * local directory listener
        * rename files
        * modify file content
        * delete files
        * publish/receive event bus events
    * P2P application
        * upload files
        * download files
        * listen to actions
        * transmit actions
        * publish/receive event bust events

### Dependencies
* [vertex](https://vertx.io)
* [apache commons-net](https://mvnrepository.com/artifact/commons-net/commons-net)
* [apache commons-io](https://mvnrepository.com/artifact/commons-io/commons-io)
* [apache-commons-cli](https://mvnrepository.com/artifact/commons-cli/commons-cli)
* [apache-commons-codec](https://mvnrepository.com/artifact/commons-codec/commons-codec/1.9)

### How to compile and run project
1) Compile
``` bash
mvn package
```
2) Run
``` bash
java -jar target/synchronizer-jar-with-dependencies.jar -p /opt/dir -d 172.18.0.10:2020 172.18.0.15:2020
```
Or (using makefile) running 4 docker containers
``` bash
make build-docker
make -j run-all
```
Execute bash inside containers and manipulate monitorable path!
``` bash
docker exec container-name-1 /bin/bash
docker exec container-name-2 /bin/bash

```

## Verticles architecture
![verticles_design](docs/verticles_design.png)
## Software design
![classDesign](docs/class_design.png)

## Actions schemes

- Modify:
``` json
    {
      "type": "MODIFY",
      "checksum": "edfdcfd4e646fe736caa2825226bf33f",
      "path": "/opt/dir/newFile.txt",
      "isDir": false,
      "timestamp": 1572730328,
      "buffer" : "this is the modifications that was made in file"
    }
 ```
- Create:
``` json
    {
      "type": "CREATE",
      "path": "/opt/dir/newFile.txt",
      "isDir": false,
      "checksum": "a063e188310b9cf711b0e251a349afc1",
      "timestamp": 1572730322,
      "buffer" : "new content is added to new file"
    }
```
- Delete:
``` json
    {
      "type": "DELETE",
      "path": "/opt/dir/newFile.txt",
      "isDir": false,
      "timestamp": 1572730328
    }
```
- Request:
``` json
    {
      "type": "REQUEST",
      "path": "/opt/dir/newFile.txt",
      "isDir": false,
      "timestamp": 1572740322
    }
```
- Response:
``` json
    {
      "type": "RESPONSE",
      "path": "/opt/dir/newFile.txt",
      "isDir": false,
      "checksum": "f8a6701de14ec3fcfd9f2fe595e9c9ed",
      "timestamp": 1572740322,
      "buffer": "this is content of requested file"
    }
```


### Implementation Guide:

##### MultiThreadedApplication
   - Initialize an application:
      ``` java
      class Application extends MultiThreadedApplication{
        @Override
        public void kill(){}
      }

      class Program{
        public void main(String [] args){
            Application app = new Application();
        }
      }
      ```
   - Add new stachostic task (executed asynchronically) to application:
      ``` java
        app.scheduleStachosticTask(new Task(){
            @Override
            public void run(){
                System.out.println("synchronizer.verticles.storage applicaiton runs stachostic task!");
            }
        });
        ```
   - Add new sequent task (executed synchronically) to application:
        ``` java
         app.scheduleSequentTask(new Task() {
            @Override
            public void run() {
                System.out.println("synchronizer.verticles.storage application executed sequent task #1");
            }
         });
         app.scheduleSequentTask(new Task() {
             @Override
             public void run() {
                System.out.println("synchronizer.verticles.storage application executed sequent task #2");
             }
         });
        ```



##### P2PApplication
* Add TCP peer to the network
``` java
    // add peer on host 10.0.0.5 and listening port 2017
    Set<Peer> peers = new HashSet<>();
    for (String device : devices) {
        String host = device.split(":")[0];
        int port = Integer.parseInt(device.split(":")[1]);
        peers.add(new Peer(host, port));
    }
    TCPPeer tcpPeer = new TCPPeer(myIpAddress, port, peers, new NetClientOptions()
                    .setReconnectAttempts(reconnectAttemps)
                    .setReconnectInterval(reconnectInterval));

    // connect to peer with send action handler
    tcpPeer.connect(peer, new SendActionHandler(action));

    // listen to peers with handler
    tcpPeer.listen(handler -> {
        if (handler instanceof NetSocket) {
            NetSocket socket = (NetSocket) handler; // will fail on runtime if handler is not a net socket

                    socket.handler(buffer -> {
                        // handle buffer
                        if (buffer == null || buffer.toString().isEmpty()) {
                            return;
                        }
                        // confirm buffer to close sender's client socket
                        socket.write(new Ack().bufferize());
                    });
        }
    });

    // broadcast action to all peers
    tcpPeer.broadcastAction(action);

    // send action to specific peer
    tcpPeer.sendAction(peer, action);

```

__Important__: after tcpPeer is deployed it is not allowed to add client-server handlers.

### Makefile
``` bash
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
```


#### Demo Time!

1) Create docker network
``` bash
# class B subnet, broadcast 172.18.255.255
docker network create --subnet=172.18.0.0/16 mynet123
```
2) Pre-Define peers IP addresses and ports
``` bash
172.18.0.10:2020
172.18.0.15:2020
```

3) Run docker containers with "synchronizer" software

Client 1:
``` bash
docker run --net mynet123 --ip 172.18.0.10 -it --rm synchronizer:latest
```
Client 2:
``` bash
docker run --net mynet123 --ip 172.18.0.15 -it --rm synchronizer:latest
```
