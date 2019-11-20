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
import synchronizer.models.Checksum;
import synchronizer.models.EventBusAddress;
import synchronizer.models.SharedDataMapAddress;
import synchronizer.models.actions.CreateAction;
import synchronizer.models.actions.DeleteAction;
import synchronizer.models.actions.ModifyAction;

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
    // local absolute path
    private final Path path;
    // observer
    private final FileAlterationObserver observer;
    // notify observables
    private final FileAlterationMonitor monitor;
    // address to publish to
    private final EventBusAddress outcomingAddress;
    // local map address to log local alternations
    private final SharedDataMapAddress localMapAddress;
    // my host (used for logging)
    private final String host;
    // local map
    private LocalMap<String, JsonObject> localMap;
    // produce file system actions to event bus
    private MessageProducer<JsonObject> producer;

    /**
     * @param path             - local path
     * @param outcomingAddress - event bus address to broadcast outcoming alternations
     * @param localMapAddress  - SharedData map address for local path structure
     */
    public ActionSenderVerticle(String myIpAddress, Path path, EventBusAddress outcomingAddress, SharedDataMapAddress localMapAddress) {

        this.outcomingAddress = outcomingAddress;
        this.localMapAddress = localMapAddress;

        this.path = path;

        this.observer = new FileAlterationObserver(path.toString());
        // interval scanning for local file system alternations
        // milliseconds
        int MONITOR_INTERVAL = 400;
        this.monitor = new FileAlterationMonitor(MONITOR_INTERVAL);

        this.host = myIpAddress;
    }

    @Override
    public void start() throws Exception {

        // local map
        this.localMap = vertx.sharedData().getLocalMap(this.localMapAddress.toString());

        // event bus
        // event bus
        EventBus eb = vertx.eventBus();

        // event bus producer
        this.producer = eb.publisher(this.outcomingAddress.toString());

        /**
         * monitorable path
         *          max number of retries
         *          attempts made
         *          action object
         *          do nothing
         *          validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
         *          create action object and publish it to the event bus
         *          validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
         *          file must exist locally
         *          validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
         *          if directory doesn't has files inside it
         *          create all files inside created dir
         *          validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
         *          validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
         *          read modified directory
         *          no files inside a directory
         *          send create empty directory action
         *          modify files inside modified files inside dir
         *          validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
         *          publish created file to event bus
         *          broadcast modify action
         *          send delete action as dir
         *          close event bus producer
         *          send create action of an empty directory
         *          create the directory itself first
         */

        /**
         * checks inside shared local map if this file was modified recently from an external source
         * if called function with argument differs in checksum, erase the file from map as it's not
         * relevant anymore (more recent changes had been taken with the file)
         * @param file
         * @return
         */

        /**
         *  // modified file must exist locally
         *         // except of a delete action
         *         // if a file is a directory the action is isValid
         *         // all inner files will be validated later
         *         // TODO: test and change this
         *         // origin file action
         *         // check if file exists in map
         *         // compare the checksums of the origin file checksum and the current file
         *         // if the checksums differ - the user has made the modification
         *         // remove origin file from map (not relevant any more)
         *         // the file was modified by an incoming action - therefore is not isValid!!
         *         // file is not in the map meaning it's not been modified by us
         *         //logger.info(String.format("%s %s", ActionSenderVerticle.this.host, actionObject.toString()));
         *         // calculate checksum and only if checksum differs publish to event bus
         *         // if no ACK was send back
         *         // resend
         *         // give up
         *         // reset attempts made
         *         // observable
         */

        FileAlterationListener listener = new FileAlterationListener() {

            // max number of retries
            private final int MAX_RETRY = 7;
            // action object
            JsonObject actionObject = new JsonObject();
            // monitorable path
            private Path path;
            // attempts made
            private int attempts = 0;

            FileAlterationListener start(Path path) {
                this.path = path;
                logger.info("starting file alternation listener");
                return this;
            }

            @Override
            public void onStart(FileAlterationObserver observer) {
                // do nothing
            }

            @Override
            public void onFileCreate(File file) {

                // validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
                if (!isValidAction(file)) {
                    return;
                }

                // create action object and publish it to the event bus
                // read file content
                Buffer fileBuffer = vertx.fileSystem().readFileBlocking(file.toPath().toString());
                actionObject = new JsonObject(new CreateAction(file.toPath(), false, fileBuffer).toJson());
                publish(actionObject);

            }

            @Override
            public void onFileChange(File file) {
                // validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
                // file must exist locally
                if (!isValidAction(file)) {
                    return;
                }

                Buffer modifiedBuffer = vertx.fileSystem().readFileBlocking(file.toPath().toString());

                if (modifiedBuffer != null) {
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
                if (!isValidAction(dir)) {
                    return;
                }

                List<String> newFiles = vertx.fileSystem().readDirBlocking(dir.toPath().toString());

                // if directory doesn't has files inside it
                if (newFiles.isEmpty()) {
                    publishCreateDir(dir);
                    return;
                }

                // create all files inside created dir
                for (String fileName : newFiles) {
                    // validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
                    if (!isValidAction(new File(fileName))) {
                        continue;
                    }
                    if (!(new File(fileName).isDirectory())) {
                        Buffer newFileBuffer = vertx.fileSystem().readFileBlocking(fileName);
                        actionObject = new JsonObject(new CreateAction(Paths.get(fileName), false, newFileBuffer).toJson());
                    } else {
                        actionObject = new JsonObject(new CreateAction(Paths.get(fileName), true, Buffer.buffer()).toJson());
                    }
                    publish(actionObject);
                }
            }

            @Override
            public void onDirectoryChange(File dir) {
                // validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
                if (!isValidAction(dir)) {
                    return;
                }

                // read modified directory
                List<String> newFiles = vertx.fileSystem().readDirBlocking(dir.toPath().toString());

                // no files inside a directory
                if (newFiles.isEmpty()) {
                    // send create empty directory action
                    publishCreateDir(dir);
                    return;
                }

                // modify files inside modified files inside dir
                for (String fileName : newFiles) {
                    // validate that the file has not been modified from an outside action. (e.g the ping-pong problem)
                    if (!isValidAction(new File(fileName))) {
                        return;
                    }
                    // publish created file to event bus
                    // broadcast modify action
                    Buffer modifiedBuffer = vertx.fileSystem().readFileBlocking(fileName);
                    actionObject = new JsonObject(new CreateAction(Paths.get(fileName), false, modifiedBuffer).toJson());
                    publish(actionObject);
                }
            }

            @Override
            public void onDirectoryDelete(File dir) {
                logger.info(String.format("Deleted dir %s", dir.toString()));

                // send delete action as dir
                actionObject = new JsonObject(new DeleteAction(dir.toPath(), true).toJson());
                // publish action to event bus
                publish(actionObject);

            }

            @Override
            public void onStop(FileAlterationObserver fileAlterationObserver) {
                // close event bus producer
            }

            /**
             *
             * @param dir - publish directory creation action to event bus
             */
            void publishCreateDir(File dir) {
                // send create action of an empty directory
                // create the directory itself first
                actionObject = new JsonObject(new CreateAction(dir.toPath(), true, Buffer.buffer()).toJson());
                publish(actionObject);
            }

            /**
             * checks inside shared local map if this file was modified recently from an external source
             * if called function with argument differs in checksum, erase the file from map as it's not
             * relevant anymore (more recent changes had been taken with the file)
             * @param file - validate local file for action
             * @return true if the action is made by the user
             */

            // TODO: if it's a directory just check if it exists localli
            boolean isValidAction(File file) {

                // if the file is a directory and it exists locally - no need to broadcast any action
                // delete actions are not validated!
                if (file.isDirectory() && vertx.fileSystem().existsBlocking(file.toString())) {
                    return false;
                }

                // origin file action
                JsonObject origin;

                logger.info(String.format("checking if path %s exists in local map",file.toString()));

                // check if file exists in map
                if ((origin = ActionSenderVerticle.this.localMap.get(file.toString())) != null) {

                    logger.info(String.format("validating action for %s", origin.toString()));

                    // get file checksum
                    String originChecksum = origin.getString("checksum");

                    // compare the checksums of the origin file checksum and the current file
                    // if the checksums differ - the user has made the modification
                    if (!Checksum.equals(originChecksum, Checksum.checksum(file.toPath()))) {
                        // remove origin file from map (not relevant any more)
                        ActionSenderVerticle.this.localMap.remove(file.toString());
                        return true;
                    } else {
                        // the file was modified by an incoming action - therefore is not isValid!!
                        logger.info(String.format("File %s is been modified by an action. Successfully encountered ping-pong problem.", file.toString()));
                        return false;
                    }
                } else {
                    return true; // file is not in the map meaning it's not been modified by us - meaning the action is valid!
                }
            }

            /**
             * publish action to event bus
             * @param actionObject
             */
            void publish(JsonObject actionObject) {
                //logger.info(String.format("%s %s", ActionSenderVerticle.this.host, actionObject.toString()));

                // calculate checksum and only if checksum differs publish to event bus
                ActionSenderVerticle.this.producer.send(actionObject, reply -> {
                    // if no ACK was send back
                    if (reply.failed()) {
                        // resend
                        if (attempts < MAX_RETRY) {
                            publish(actionObject);
                        } else {
                            // give up
                            attempts = 1;
                        }
                    } else {
                        // reset attempts made
                        attempts = 1;
                    }
                });
                actionObject.clear();
            }

        }.start(this.path);

        observer.addListener(listener);
        monitor.addObserver(observer);
        logger.debug("Starting ActionsSenderVerticle");
        monitor.start();
        logger.info(String.format("%s publishing actions to event bus address: %s", this.host, outcomingAddress.toString()));
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        // TODO: vertx told not to call stop()
        super.stop(stopFuture);
        logger.error(String.format("%s ActionSenderVerticle failed %s", this.host, stopFuture.result()));
    }

}
