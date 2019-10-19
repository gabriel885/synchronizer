## סדנה בתקשורת מחשבים 20588
![assignment](docs/assignment.png)

## Objective
Java program called "synchonizer" responsible for synchronizing content 
between other computers that runs "synchronizer".

### Principles
 * FS watch
    * fs_windows
    * fs_unix
    * fs_eventTypes
 * FS Walk (walk recursively descends path)
 
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
    
    
### Design
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

### How to install
1) mvn install
2) run .jar
### Imitating isolated hosts docker containers (each pod runs a container of "synchronizer")
![pods](docs/pods.png)


### API

### NOTES!

RenameAction is not a model. Refactor models package or move all non-models to another package.

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
//                        System.out.println("running sequent service");
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


