package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.json.JsonObject;

import io.vertx.core.shareddata.LocalMap;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.*;

import java.io.File;
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

    private final int  MONITOR_INTERVAL = 400; // milliseconds

    /**
     *
     * @param path - local path
     * @param address - event bus address to broadcast outcoming alternations
     * @param localMapAddress - SharedData map address for local path structure
     */
    public ActionSenderVerticle(Path path, EventBusAddress address, SharedDataMapAddress localMapAddress){

        EventBus eb = Vertx.vertx().eventBus();
        this.producer = eb.publisher(address.toString());
        logger.info("publishing to: " + address.toString());

        this.observer = new FileAlterationObserver(path.toString());
        this.monitor = new FileAlterationMonitor(MONITOR_INTERVAL);


        this.listener = new FileAlterationListener() {

            // action object
            JsonObject actionObject = new JsonObject();


               @Override
               public void onStart(org.apache.commons.io.monitor.FileAlterationObserver observer) {

                   // sync initial path state?
                   // save in shared data the initial hierarchy?
               }


               @Override
               public void onFileCreate(File file) {
                   // code for processing creation event
                   logger.info(String.format("File %s created", file.toString()));
                   //ActionSenderVerticle.this.producer.write(new CreateAction(null, file));

                   // create action object and send to the event bus
                   actionObject.put("CREATE",file.getPath());
                   ActionSenderVerticle.this.producer.write(actionObject);
                   logger.debug(actionObject.toString());
                   actionObject.clear();

                   // update local.path.structure SharedData
                   vertx.sharedData().getLocalMap(localMapAddress.toString()).put(file.getName(),new synchronizer.models.File(file));

               }

               @Override
               public void onFileChange(File file) {
                   // code for processing change event
                   logger.info(String.format("File %s changed", file.toString()));
                   // ActionSenderVerticle.this.producer.write(new ModifyAction(null, file));
                   actionObject.put("MODIFY",file.getPath());
                   ActionSenderVerticle.this.producer.write(actionObject);
                   logger.debug(actionObject.toString());
                   actionObject.clear();


                   // update last modification time of a file
                   // TODO: throws NullPointerException Error
                   //
                  // synchronizer.models.File f = (synchronizer.models.File)vertx.sharedData().getLocalMap(localMapAddress.toString()).get(file.toString());
                   //f.updateLastModification();
               }

               @Override
               public void onFileDelete(File file) {

                   // code for processing deletion event
                   logger.info(String.format("File %s deleted", file.toString()));
                   //ActionSenderVerticle.this.producer.write(new DeleteAction(null, file));
                   actionObject.put("DELETE",file.getPath());
                   ActionSenderVerticle.this.producer.write(actionObject);
                   logger.debug(actionObject.toString());
                   actionObject.clear();

                   // update local.path.structure SharedData
                   vertx.sharedData().getLocalMap(localMapAddress.toString()).remove(file.toString());
               }


               @Override
               public void onDirectoryCreate(File dir) {
                   logger.info(String.format("Directory %s created", dir.toString()));
                   //ActionSenderVerticle.this.producer.write(new CreateAction(null, dir));
                   actionObject.put("CREATE",dir.getPath());
                   ActionSenderVerticle.this.producer.write(actionObject);
                   logger.debug(actionObject.toString());
                   actionObject.clear();


               }

               @Override
               public void onDirectoryChange(File dir) {
                   logger.info(String.format("Directory %s changed", dir.toString()));
                   //ActionSenderVerticle.this.producer.write(new ModifyAction(null, dir));
                   actionObject.put("MODIFY",dir.getPath());
                   ActionSenderVerticle.this.producer.write(actionObject);
                   logger.debug(actionObject.toString());
                   actionObject.clear();
               }

               @Override
               public void onDirectoryDelete(File dir) {
                   logger.info(String.format("Directory %s deleted", dir.toString()));
                   //ActionSenderVerticle.this.producer.write(new DeleteAction(null, dir));
                   actionObject.put("DELETE",dir.getPath());
                   logger.info(actionObject.toString());
                   ActionSenderVerticle.this.producer.write(actionObject);
                   logger.debug(actionObject.toString());
                   actionObject.clear();
               }

               @Override
               public void onStop(FileAlterationObserver fileAlterationObserver) {

               }

           };


    }

    @Override
    public void start(Future<Void> startFuture) throws Exception{
        observer.addListener(listener);
        monitor.addObserver(observer);
        logger.debug("Starting ActionsSenderVerticle");
        monitor.start();
        startFuture.complete();
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception{
        // TODO: vertx told not to call stop()
        super.stop(stopFuture);
        logger.error(String.format("ActionSenderVerticle failed %s", stopFuture.result()));
    }


}
