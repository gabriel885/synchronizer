## סדנה בתקשורת מחשבים 20588
![assignment](docs/assignment.png)

## Objective
Java program called "synchonizer" responsible for synchronizing path file data  
between multiple computers.

## Prerequesites
 * JDK 1.8
 * Docker (optional)
    
### Principles
 * EventBus Actions
    *
    *
    *
 * SharedData 
    * local.path.structure
    * global.path.structure
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
 * Multi Threaded Server
    * Storage Application
        * local directory listener
        * chunk files (for upload)
        * rename files
        * modify file content
        * delete files
        * compress chunks
        * delta comparisons
    * P2P application 
        * upload files
        * download files
        * listen to actions
        * transmit actions

## Adding computers to the network
* each computer has a unique ID. To add computers someone from 
the network will need to add the unique ID and establish a connection.

## Discovery Protocol
Local discovery protocol

### Dependencies
* [vertex](https://vertx.io)
* [apache commons-net]()
* [apache commons-io]()

### How to install
1) mvn install
2) run .jar
### Imitating isolated hosts docker containers (each pod runs a container of "synchronizer")
![pods](docs/pods.png)

## When to use verticles and when to use threads?
## verticles vs Threads
## Task vs Service



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
```
##### P2PApplication
        
        
        
#### Demo Time!

1) Create docker network
```bash
# class B subnet, broadcast 172.18.255.255
docker network create --subnet=172.18.0.0/16 mynet123
```
2) Define peers IP addresses and ports in __peers.txt__ file
```bash
172.18.0.10:2020
172.18.0.15:2020
172.18.0.17:2020
172.18.0.20:2020
```

3) run docker containers with "synchronizer" software

Client 1:
```bash
docker run --net mynet123 --ip 172.18.0.10 -it synchronizer:latest  
```
Client 2:
```bash
docker run --net mynet123 --ip 172.18.0.15 -it synchronizer:latest  
```
Client 3:
```bash
docker run --net mynet123 --ip 172.18.0.17 -it synchronizer:latest  
```
Client 4:
```bash
docker run --net mynet123 --ip 172.18.0.20 -it synchronizer:latest  
```
        
### NOTES!

RenameAction is not a model. Refactor models package or move all non-models to another package.

``` java


// RUN ALL STORAGE SERVICES!!!!!
                // System.out.println("renaming file foo.txt");
                // addSequentService(new RenameFileService("foo.txt","bar.txt"));
                // addSequentService(new DeleteFileService(Paths.get("foo.txt")));

                //                // fs watcher
//                addStachosticService(new MonitorFileSystemActionsService(path));
//
//                // create 3 random files
//                addSequentService(new ImitateFileCreations(path,3));
//
//
//
//                addSequentService(new Service(){
//
//                    @Override
//                    public void run() {
//                        System.out.println("running sequent task");
//                    }
//                });
//
//                addSequentService(new RenameFileService(Paths.get(path.toString(),"bar.txt").toString(),Paths.get(path.toString(),"newBar.txt").toString()));
//
//                String[] filenames = {"hello.txt", "gabriel.txt", "ilya.txt"};
//                addSequentService(new ImitateFileCreations(path,filenames));
//
//                addSequentService(new RenameFileService(Paths.get(path.toString(),"hello.txt"),"newHello.txt"));
//
//                addSequentService(new RenameFileService(Paths.get(path.toString(),"gabriel.txt"),"newGabriel.txt"));
//
//                addSequentService(new RenameFileService(Paths.get(path.toString(),"ilya.txt"),"newIlya.txt"));




```
