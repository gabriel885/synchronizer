package synchronizer.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import synchronizer.models.*;

import java.io.File;
import java.nio.file.Path;

// ActionSenderVerticle watch for local file system alternations.
// It publishes the actions to the event bus
// Uses Observable pattern
public class ActionSenderVerticle extends AbstractVerticle {

    // produce file system actions to event bus
    private MessageProducer<JsonObject> producer;

    // passing actionObject to event bus
    private JsonObject actionObject;

    // observer
    private FileAlterationObserver observer;
    // notify observables
    private FileAlterationMonitor monitor;
    // observable
    private FileAlterationListener listener;

    private final int  MONITOR_INTERVAL = 400; // milliseconds


    /**
     *
     * @param path
     * @param address
     * @param mapAddress
     */
    public ActionSenderVerticle(Path path, EventBusAddress address, SharedDataMapAddress mapAddress){
        this.actionObject = new JsonObject();

        EventBus eb = Vertx.vertx().eventBus();
        this.producer = eb.publisher(address.toString());
        System.out.println("publishing to: " + address.toString());

        this.observer = new FileAlterationObserver(path.toString());
        this.monitor = new FileAlterationMonitor(MONITOR_INTERVAL);

        this.actionObject = new JsonObject();

        this.listener = new FileAlterationListener(){

               @Override
               public void onStart(org.apache.commons.io.monitor.FileAlterationObserver observer) {

                   // sync initial path state?
                   // save in shared data the initial hierarchy?
               }


               @Override
               public void onFileCreate(File file) {
                   // code for processing creation event
                   System.out.println(String.format("File %s created", file.toString()));
                   //ActionSenderVerticle.this.producer.write(new CreateAction(null, file));
                   actionObject.put("CREATE",file.getPath());
                   ActionSenderVerticle.this.producer.write(actionObject);
                   actionObject.clear();

               }

               @Override
               public void onFileChange(File file) {
                   // code for processing change event
                   System.out.println(String.format("File %s changed", file.toString()));

                   // ActionSenderVerticle.this.producer.write(new ModifyAction(null, file));
                   actionObject.put("MODIFY",file.getPath());
                   ActionSenderVerticle.this.producer.write(actionObject);
                   actionObject.clear();

                   // update last modification time of a file
                   synchronizer.models.File f = (synchronizer.models.File)vertx.sharedData().getLocalMap(mapAddress.toString()).get(file.toString());
                   f.updateLastModification();
               }

               @Override
               public void onFileDelete(File file) {

                   // code for processing deletion event
                   System.out.println(String.format("File %s deleted", file.toString()));
                   //ActionSenderVerticle.this.producer.write(new DeleteAction(null, file));
                   actionObject.put("DELETE",file.getPath());
                   ActionSenderVerticle.this.producer.write(actionObject);
                   actionObject.clear();

                   // update SharedDataApi
                   vertx.sharedData().getLocalMap(mapAddress.toString()).remove(file.toString());
               }


               @Override
               public void onDirectoryCreate(File dir) {
                   System.out.println(String.format("Directory %s created", dir.toString()));
                   //ActionSenderVerticle.this.producer.write(new CreateAction(null, dir));
                   actionObject.put("CREATE",dir.getPath());
                   ActionSenderVerticle.this.producer.write(actionObject);
                   actionObject.clear();
               }

               @Override
               public void onDirectoryChange(File dir) {
                   System.out.println(String.format("Directory %s changed", dir.toString()));
                   //ActionSenderVerticle.this.producer.write(new ModifyAction(null, dir));
                   actionObject.put("MODIFY",dir.getPath());
                   ActionSenderVerticle.this.producer.write(actionObject);
                   actionObject.clear();
               }

               @Override
               public void onDirectoryDelete(File dir) {
                   System.out.println(String.format("Directory %s deleted", dir.toString()));
                   //ActionSenderVerticle.this.producer.write(new DeleteAction(null, dir));
                   actionObject.put("DELETE",dir.getPath());
                   ActionSenderVerticle.this.producer.write(actionObject);
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
        System.out.println("Starting ActionsSenderVerticle");
        monitor.start();
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception{
        super.stop(stopFuture);
    }


}
