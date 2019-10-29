## סדנה בתקשורת מחשבים 20588
![assignment](docs/assignment.png)

## Objective
Java program called "synchonizer" responsible for synchronizing path file data  
between multiple computers.

## Prerequesites
 * JDK 1.8 
 * Maven
 * Docker (optional)
    
### Principles
 * EventBus Actions
    *
    *
    *
 * SharedData 
    * local.path
    * global.path
 * Events
    * localChangeDetected
    * remoteChangeDetected
    * StateChanged
    * ConfigSaved
    * DownloadProgress
    * FolderSummary
    * FolderWatchStateChanged
    * FolderScanProgress
    * DeviceDiscovered
    * DeviceConnected
    * DeviceDisconnected
    * DeviceRejected
    * DevicePaused
    * DeviceResumed
    
 * Service
    * tcpDial
    * tcpListen
    
 * Verification
    * add computer with Unique ID and IP
    
    
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
* [apache commons-net]()
* [apache commons-io]()
* [apache-commons-cli]()

### How to compile and run project
1) Compile
```bash
mvn package
```
2) Run
```bash
java -jar target/synchronizer-jar-with-dependencies.jar -p /Users/gabrielmunits/opt/dir -d 172.18.0.10:2020 172.18.0.15:2020
```
### Imitating isolated hosts docker containers (each pod runs a container of "synchronizer")
![pods](docs/pods.png)


## Action vs Message
Action represent modification type like rename,create or delete action.
Message on the other side contains file raw data (deltas).
Ack/Nack are action type representing acknowledgement of a message - validated acceptance 
of file data.

## Round Robin Algorithm

### Implementation Guide:

## scatter-gather protocol
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
                System.out.println("storage applicaiton runs stachostic task!");
            }
        });
        ```
   - Add new sequent task (executed synchronically) to application:
        ``` java
         app.scheduleSequentTask(new Task() {
            @Override
            public void run() {
                System.out.println("storage application executed sequent task #1");
            }
         });
         app.scheduleSequentTask(new Task() {
             @Override
             public void run() {
                System.out.println("storage application executed sequent task #2");
             }
         });
        ```
        
##### StorageApplication

* __Scan periodically file system__
```java
    // scan local file system path structure every 10 seconds.
    vertx.setPeriodic(10000,v->{
        vertx.deployVerticle(new LocalFileSystemWalkerVerticle(path));
    });
```
* __Consume EventBus messages__
```java
 // consume path map
 vertx.deployVerticle(new Verticle() {
     @Override
     public Vertx getVertx() {
         return null;
     }

     @Override
     public void init(Vertx vertx, Context context) {

     }

     @Override
     public void start(Future<Void> startFuture) throws Exception {
         EventBus eb = vertx.eventBus();
         eb.consumer("files", message ->{
             logger.info(message.body());
         });
     }

     @Override
     public void stop(Future<Void> stopFuture) throws Exception {
     }
 });
* __Pubblish EventBus messages__

```
##### P2PApplication
* Add TCP peer to the network
```java
    // add peer on host 10.0.0.5 and listening port 2017
    TCPPeer tcpPeer = new TCPPeer(myHost,port,new NetClientOptions().setReconnectAttempts(5).setReconnectInterval(5000));
    // deploy tcp peer verticle
    // on deploy peer will connect to other peers 
    vertx.deployVerticle(tcpPeer);

```

* Create server handlers for TCP peer (listening for incoming actions from other peers)
```java

// server handlers
// default handler is log hander (because server must have at least one handler registered)
ServerHandlers serverHandlers = new ServerHandlers(new LogHandler());

serverHandlers.add(new ActionHandler() {
            @Override
            public void handle(NetSocket event) {

            }
});

* Create client handlers for TCP peer (sending outcoming actions to other peers)
```java


```


// listen with server configurations and register server handlers
listen(serverHandlers);
```

* Consume EventBust action messages
```java


```



        
        
#### Demo Time!

1) Create docker network
```bash
# class B subnet, broadcast 172.18.255.255
docker network create --subnet=172.18.0.0/16 mynet123
```
2) Pre-Define peers IP addresses and ports
```bash
172.18.0.10:2020
172.18.0.15:2020
172.18.0.17:2020
172.18.0.20:2020
```

3) Run docker containers with "synchronizer" software

Client 1:
```bash
docker run --net mynet123 --ip 172.18.0.10 -it --rm synchronizer:latest  
```
Client 2:
```bash
docker run --net mynet123 --ip 172.18.0.15 -it --rm synchronizer:latest  
```
Client 3:
```bash
docker run --net mynet123 --ip 172.18.0.17 -it --rm synchronizer:latest  
```
Client 4:
```bash
docker run --net mynet123 --ip 172.18.0.20 -it --rm synchronizer:latest  
```
        
### Makefile
```bash
# build maven project
build:
	@mvn package

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
run-all: run-docker-client-1 run-docker-client-2 run-docker-client-3 run-docker-client-4

# run docker client 1
run-docker-client-1:
	@docker run --net mynet123 --ip 172.18.0.10 -it --rm synchronizer:latest

run-docker-client-2:
	@docker run --net mynet123 --ip 172.18.0.15 -it --rm synchronizer:latest

run-docker-client-3:
	@docker run --net mynet123 --ip 172.18.0.17 -it --rm synchronizer:latest

run-docker-client-4:
	@docker run --net mynet123 --ip 172.18.0.20 -it --rm synchronizer:latest


# kill all docker clients
kill:
	docker stop $(docker ps -q --filter ancestor=<synchronizer:latest> )

```
