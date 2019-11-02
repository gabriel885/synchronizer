package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

import io.vertx.core.net.SocketAddress;
import io.vertx.core.shareddata.LocalMap;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.*;
import synchronizer.models.actions.CreateAction;
import synchronizer.models.actions.DeleteAction;
import synchronizer.models.actions.ModifyAction;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Path;


// TODO: IMPORTANT
// this.listener is not a verticle. therefore if it fails
// we loose our listener. maybe the listener needs to be a verticle?


// ActionSenderVerticle watch for local file system alternations.
// It publishes the actions to the event bus.
// It uses Observable pattern.
public class ActionSenderVerticle extends AbstractVerticle {

    // logger
    private static final Logger logger = LogManager.getLogger(ActionSenderVerticle.class);

    // produce file system actions to event bus
    private MessageProducer<JsonObject> producer;

    // passing actionObject to event bus
    //private JsonObject actionObject;

    // observer
    private FileAlterationObserver observer;
    // notify observables
    private FileAlterationMonitor monitor;
    // observable
    private FileAlterationListener listener;

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
    public ActionSenderVerticle(Path path, EventBusAddress outcomingAddress, SharedDataMapAddress localMapAddress){

        this.outcomingAddress = outcomingAddress;
        this.localMapAddress = localMapAddress;

        this.observer = new FileAlterationObserver(path.toString());
        this.monitor = new FileAlterationMonitor(MONITOR_INTERVAL);

        this.host = getHost();
    }

    @Override
    public void start() throws Exception{

        // initialize vertx stuff
        this.eb = vertx.eventBus();
        this.producer = this.eb.publisher(this.outcomingAddress.toString());

        this.listener = new FileAlterationListener() {
            // max number of retries
            private int MAX_RETRY=7;
            // attempts made
            private int attempts=0;

            // action object
            JsonObject actionObject = new JsonObject();


            @Override
            public void onStart(org.apache.commons.io.monitor.FileAlterationObserver observer) {

                // sync initial path state?
                // save in shared data the initial hierarchy?
            }


            @Override
            public void onFileCreate(File file)  {
                // create action object and send to the event bus
                actionObject = new JsonObject(new CreateAction(file.toPath()).toJson());
                publish(actionObject);

                // update local.path.structure SharedData
                vertx.sharedData().getLocalMap(localMapAddress.toString()).put(file.getName(),new synchronizer.models.File(file));

                //logger.info(vertx.sharedData().getLocalMap(localMapAddress.toString()).entrySet());
            }

            @Override
            public void onFileChange(File file) {
                actionObject = new JsonObject(new ModifyAction(file.toPath()).toJson());
                publish(actionObject);
            }

            @Override
            public void onFileDelete(File file) {
                actionObject = new JsonObject(new DeleteAction(file.toPath()).toJson());
                publish(actionObject);

                // update local.path.structure SharedData
                vertx.sharedData().getLocalMap(localMapAddress.toString()).remove(file.toString());
            }


            @Override
            public void onDirectoryCreate(File dir) {
                actionObject = new JsonObject(new CreateAction(dir.toPath()).toJson());
                publish(actionObject);
            }

            @Override
            public void onDirectoryChange(File dir) {
                //ActionSenderVerticle.this.producer.write(new ModifyAction(null, dir));
                actionObject = new JsonObject(new ModifyAction(dir.toPath()).toJson());
                publish(actionObject);
            }

            @Override
            public void onDirectoryDelete(File dir) {
                //ActionSenderVerticle.this.producer.write(new DeleteAction(null, dir));
                actionObject = new JsonObject(new DeleteAction(dir.toPath()).toJson());
                publish(actionObject);
            }

            @Override
            public void onStop(FileAlterationObserver fileAlterationObserver) {
                // close event bus producer
            }

            /**
             * publish action to event bus
             * @param actionObject
             */
            public void publish(JsonObject actionObject){
                logger.info(String.format("%s %s", ActionSenderVerticle.this.host, actionObject.toString()));

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

                // TODO: update local path shared data local map
                // update last modification time of a file
                // TODO: throws NullPointerException Error
                //
                // synchronizer.models.File f = (synchronizer.models.File)vertx.sharedData().getLocalMap(localMapAddress.toString()).get(file.toString());
                //f.updateLastModification();

            }

        };

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

    /**
     * get local host
     * NOTE: do not call this in event-loop - might block it
     * @return
     */
    public String getHost(){
        InetAddress inetAddress;
        try{
            inetAddress = InetAddress.getLocalHost();
        }catch (Exception e){
            // cry
            return "Unknown host";
        }
        return inetAddress.getHostAddress();

    }
}
