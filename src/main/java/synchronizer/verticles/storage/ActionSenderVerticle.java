package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.EventBusAddress;
import synchronizer.models.SharedDataMapAddress;
import synchronizer.models.actions.CreateAction;
import synchronizer.models.actions.DeleteAction;
import synchronizer.models.actions.ModifyAction;
import synchronizer.models.Checksum;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


// ActionSenderVerticle watch for local file system alternations.
// It publishes the actions to the event bus.
// It uses Observable pattern.
public class ActionSenderVerticle extends AbstractVerticle {

    // logger
    private static final Logger logger = LogManager.getLogger(ActionSenderVerticle.class);

    // produce file system actions to event bus
    private MessageProducer<JsonObject> producer;

    // local absolute path
    private Path path;

    // observer
    private FileAlterationObserver observer;

    // notify observables
    private FileAlterationMonitor monitor;

    // observable
    private FileAlterationListener listener;

    // local map
    protected LocalMap<String, JsonObject> localMap;

    // event bus
    private EventBus eb;

    // address to publish to
    private EventBusAddress outcomingAddress;

    // local map address to log local alternations
    private SharedDataMapAddress localMapAddress;


    // interval scanning for local file system alternations
    private final int  MONITOR_INTERVAL = 400; // milliseconds

    // my host (used for logging)
    private String host;

    /**
     *
     * @param path - local path
     * @param outcomingAddress - event bus address to broadcast outcoming alternations
     * @param localMapAddress - SharedData map address for local path structure
     */
    public ActionSenderVerticle(String myIpAddress,Path path, EventBusAddress outcomingAddress, SharedDataMapAddress localMapAddress){

        this.outcomingAddress = outcomingAddress;
        this.localMapAddress = localMapAddress;

        this.path = path;

        this.observer = new FileAlterationObserver(path.toString());
        this.monitor = new FileAlterationMonitor(MONITOR_INTERVAL);

        this.host = myIpAddress;
    }

    @Override
    public void start() throws Exception{

        // local map
        this.localMap = vertx.sharedData().getLocalMap(this.localMapAddress.toString());

        // event bus
        this.eb = vertx.eventBus();

        // event bus producer
        this.producer = this.eb.publisher(this.outcomingAddress.toString());

        this.listener = new FileAlterationListener() {

            // monitorable path
            private Path path;
            // max number of retries
            private int MAX_RETRY=7;
            // attempts made
            private int attempts=0;

            // action object
            JsonObject actionObject = new JsonObject();

            public FileAlterationListener start(Path path){
                this.path = path;
                logger.info("starting file alternation listener");
                return this;
            }

            @Override
            public void onStart(org.apache.commons.io.monitor.FileAlterationObserver observer) {
                // do nothing
            }

            @Override
            public void onFileCreate(File file)  {

                // validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
                if (!validAction(file)){
                    return;
                }

                // create action object and publish it to the event bus
                Buffer fileBuffer = vertx.fileSystem().readFileBlocking(file.toPath().toString());
                actionObject = new JsonObject(new CreateAction(file.toPath(), false, fileBuffer).toJson());
                publish(actionObject);

            }

            @Override
            public void onFileChange(File file) {
                // validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
                // file must exist locally
                if (!validAction(file)){
                    return;
                }
                Buffer modifiedBuffer = vertx.fileSystem().readFileBlocking(file.toPath().toString());
                if (modifiedBuffer!=null){
                    actionObject = new JsonObject(new ModifyAction(file.toPath(), false, modifiedBuffer).toJson());
                    publish(actionObject);
                }

            }

            @Override
            public void onFileDelete(File file) {
                actionObject = new JsonObject(new DeleteAction(file.toPath(), false).toJson());
                publish(actionObject);
            }

            @Override
            public void onDirectoryCreate(File dir) {
                // validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
                if (!validAction(dir)){
                    return;
                }

                List<String> newFiles = vertx.fileSystem().readDirBlocking(dir.toPath().toString());

                // if directory doesn't has files inside it
                if (newFiles.isEmpty()){
                    publishCreateDir(dir);
                    return;
                }

                // create all files inside created dir
                for (String fileName : newFiles){
                    // validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
                    if (!validAction(new File(fileName))){
                        continue;
                    }
                    if (!(new File(fileName).isDirectory())){
                        Buffer newFileBuffer = vertx.fileSystem().readFileBlocking(fileName);
                        actionObject = new JsonObject(new CreateAction(Paths.get(fileName), false, newFileBuffer).toJson());
                    }else{
                        actionObject = new JsonObject(new CreateAction(Paths.get(fileName), true, Buffer.buffer()).toJson());
                    }
                    publish(actionObject);
                }
            }

            @Override
            public void onDirectoryChange(File dir) {
                // validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
                if (!validAction(dir)){
                    return;
                }
                // read modified directory
                List<String> newFiles = vertx.fileSystem().readDirBlocking(dir.toPath().toString());

                // no files inside a directory
                if (newFiles.isEmpty()){
                    // send create empty directory action
                    publishCreateDir(dir);
                    return;
                }

                // modify files inside modified files inside dir
                for (String fileName: newFiles){
                    // validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
                    if (!validAction(new File(fileName))){
                        return;
                    }
                    // publish created file to event bus
                    // broadcast modify action
                    Buffer modifiedBuffer = vertx.fileSystem().readFileBlocking(fileName);
                    actionObject = new JsonObject(new CreateAction(Paths.get(fileName),false,modifiedBuffer).toJson());
                    publish(actionObject);
                }
            }

            @Override
            public void onDirectoryDelete(File dir) {
                logger.info(String.format("Deleted dir %s", dir.toString()));

                // send delete action as dir
                actionObject = new JsonObject(new DeleteAction(dir.toPath(),true).toJson());
                publish(actionObject);

            }

            @Override
            public void onStop(FileAlterationObserver fileAlterationObserver) {
                // close event bus producer
            }

            /**
             *
             * @param dir
             */
            public void publishCreateDir(File dir){
                // send create action of an empty directory
                // create the directory itself first
                actionObject = new JsonObject(new CreateAction(dir.toPath(), true, Buffer.buffer()).toJson());
                publish(actionObject);
            }

            /**
             * checks inside shared local map if this file was modified recently from an external source
             * if called function with argument differs in checksum, erase the file from map as it's not
             * relevant anymore (more recent changes had been taken with the file)
             * @param file
             * @return
             */
            public boolean validAction(File file){

                // modified file must exist locally
                // except of a delete action
                if (!vertx.fileSystem().existsBlocking(file.toString())){
                    return false;
                }

                // if a file is a directory the action is valid
                // all inner files will be validated later
                if (file.isDirectory()){
                    return true;
                }

                // origin file action
                JsonObject origin;

                // check if file exists in map
                if ((origin = ActionSenderVerticle.this.localMap.get(file.toString()))!=null){

                    String originChecksum = origin.getString("checksum");

                    if (origin.getBoolean("isDir") == true){

                    }
                    // compare the checksums of the origin file checksum and the current file
                    // if the checksums differ - the user has made the modification
                    if (!Checksum.equals(originChecksum, Checksum.checksum(file.toPath()))){
                        // remove origin file from map (not relevant any more)
                        ActionSenderVerticle.this.localMap.remove(file.toString());
                        return true;
                    }
                    else{
                        // the file was modified by an incoming action - therefore is not valid!!
                        logger.info(String.format("File %s is been modified by an action", file.toString()));
                        return false;
                    }
                }
                else{
                    return true; // file is not in the map meaning it's not been modified by us
                }
            }

            /**
             * publish action to event bus
             * @param actionObject
             */
            public void publish(JsonObject actionObject){
                //logger.info(String.format("%s %s", ActionSenderVerticle.this.host, actionObject.toString()));

                // calculate checksum and only if checksum differs publish to event bus
                ActionSenderVerticle.this.producer.send(actionObject, reply->{
                    // if no ACK was send back
                    if (reply.failed()){
                        // resend
                        if (attempts<MAX_RETRY){
                            publish(actionObject);
                        }else{
                            // give up
                            attempts=1;
                        }
                    }else{
                        // reset attempts made
                        attempts=1;
                    }
                });
                actionObject.clear();
            }

        }.start(this.path);

        observer.addListener(listener);
        monitor.addObserver(observer);
        logger.debug("Starting ActionsSenderVerticle");
        monitor.start();
        logger.info(String.format("%s publishing actions to event bus address: %s", this.host ,outcomingAddress.toString()));
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception{
        // TODO: vertx told not to call stop()
        super.stop(stopFuture);
        logger.error(String.format("%s ActionSenderVerticle failed %s", this.host, stopFuture.result()));
    }

}
