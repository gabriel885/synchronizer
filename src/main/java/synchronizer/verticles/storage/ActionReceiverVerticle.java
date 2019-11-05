package synchronizer.verticles.storage;import io.vertx.core.AbstractVerticle;import io.vertx.core.Future;import io.vertx.core.buffer.Buffer;import io.vertx.core.eventbus.EventBus;import io.vertx.core.eventbus.MessageConsumer;import io.vertx.core.json.JsonObject;import io.vertx.core.shareddata.LocalMap;import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;import synchronizer.models.EventBusAddress;import synchronizer.models.File;import synchronizer.models.SharedDataMapAddress;import synchronizer.models.actions.Ack;import synchronizer.models.actions.Action;import synchronizer.models.actions.ActionType;import synchronizer.models.actions.Nack;import java.net.InetAddress;import java.nio.file.Path;import java.nio.file.Paths;/** * Class responsible for listening incoming actions * */// responsible for listening for incoming actionspublic class ActionReceiverVerticle extends AbstractVerticle {    // logger    private static final Logger logger = LogManager.getLogger(ActionReceiverVerticle.class);    // global map    protected LocalMap<String, File> globalMap;    // vertx event bus    private EventBus eb;    // event bus address to receive actions from    private EventBusAddress address;    // consume file system actions from event bus    private MessageConsumer<JsonObject> consumer;    // object received from event bus    private JsonObject actionObject;    // local synchronized path    private Path path;    // my host    private String host;    /**     *     * @param path - local path     * @param address - event bus address to listen for incoming alternations     * @param globalMapAddress - SharedData map address for global path structure (what's received)     */    public ActionReceiverVerticle(Path path, EventBusAddress address, SharedDataMapAddress globalMapAddress){        this.path = path;        this.address = address;        this.host = getHost();    }    @Override    public void start(Future<Void> startFuture) throws Exception{        // global map address        this.globalMap = vertx.sharedData().getLocalMap("global.path");        this.eb = vertx.eventBus();        this.consumer = eb.consumer(this.address.toString());        this.consumer.handler(actionReceived ->{            if (!Action.valid(actionReceived.body())){                logger.info(String.format("Received invalid JsonObject %s", actionReceived.body().toString()));                actionReceived.reply(new Nack().toJson());                return;            }            // reply with ack            actionReceived.reply(new Ack().toJson());            logger.info(String.format("%s received action %s from event bus", this.host,actionReceived.body().toString() ));            ActionType actionType = ActionType.getType(actionReceived.body().getString("type"));            actionObject = actionReceived.body();            // TODO: fix File model            // add relative file to received path            File f = new File(actionObject);            Buffer fileBuffer;            boolean isDir;            // TODO: fix localMap.put()!!! otherwise map will be corrupted            switch (actionType){                case DELETE:                    // update globalMapAddress                    // deploy delete file task                    String fileToDelete = actionObject.getString("path");                    logger.info(String.format("deploying delete file verticle: %s", fileToDelete));                    vertx.deployVerticle(new DeleteFileVerticle(fileToDelete));                    // remove from global map                    this.globalMap.remove(f.getFileName());                    break;                case CREATE:                    // save buffer to local file in path                    String fileToCreate = actionObject.getString("path");                    fileBuffer = Buffer.buffer(actionObject.getString("buffer"));                    isDir = actionObject.getBoolean("isDir");                    logger.info(String.format("deploying create file verticle: %s", fileToCreate));                    vertx.deployVerticle(new CreateFileVerticle(Paths.get(fileToCreate), isDir,fileBuffer));                    // TODO: ensure putted values are valid!!                    this.globalMap.put(f.getFileName(),f);                    break;                case MODIFY:                    String fileToModify = actionObject.getString("path");                    fileBuffer = Buffer.buffer(actionObject.getString("buffer"));                    isDir = actionObject.getBoolean("isDir");                    logger.info(String.format("deploying create file verticle: %s", fileToModify));                    vertx.deployVerticle(new CreateFileVerticle(Paths.get(fileToModify), isDir, fileBuffer));                    // TODO: ensure putted values are valid!!                    this.globalMap.put(f.getFileName(),f);                    break;                case RESPONSE:                    String fileToResponse = actionObject.getString("path");                    fileBuffer = Buffer.buffer(actionObject.getString("buffer"));                    isDir = actionObject.getBoolean("isDir");                    logger.info(String.format("deploying create file verticle: %s", fileToResponse));                    vertx.deployVerticle(new CreateFileVerticle(Paths.get(fileToResponse), isDir, fileBuffer));                    // update global map                    this.globalMap.put(f.getFileName(),f);                    break;                default:                    logger.warn(String.format("%s received unknown action type from message: %s",this.host,actionReceived.body().toString() ));                    break;            }        });        // consuming file system action events        logger.info(String.format("%s consuming actions from event bus address: %s",this.host, this.address.toString()));    }    @Override    public void stop(Future<Void> stopFuture) throws Exception{        super.stop(stopFuture);    }    public String getHost(){        InetAddress inetAddress;        try{            inetAddress = InetAddress.getLocalHost();        }catch (Exception e){            // cry            return "Unknown host";        }        return inetAddress.getHostAddress();    }}