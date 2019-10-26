package synchronizer.verticles.p2p;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.EventBusAddress;

import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;


// TODO: ERASE THIS!!!
public class TCPServerVerticle extends AbstractVerticle{

    // logger
    private static final Logger logger = LogManager.getLogger(TCPServerVerticle.class);

    // TCP Server
    private NetServer server;

    // TCP Server options (port and host)
    private NetServerOptions serverOptions;

    // TCP Client
    //private List<NetClient> clients;

    private NetClient client;

    // TCP Client options (all the computers to connect to)
    private NetClientOptions clientOptions;


    // InetAddress
    private InetSocketAddress inetSocketAddress;

    // Consume file system actions from event bus
    private MessageConsumer<JsonObject> consumer;

    // Consume outcoming file system actions from event bus and write to socket
    private EventBusAddress outcomingActions;

    // Produce incoming file system actions received from socket to event bus
    private EventBusAddress incomingActions;


    public TCPServerVerticle(InetSocketAddress socketAddress, EventBusAddress outcomingFileSystemActions, EventBusAddress incomingFileSystemActions){
        // server
        this.serverOptions = new NetServerOptions().setPort(socketAddress.getPort()).setLogActivity(true);
        this.server = Vertx.vertx().createNetServer();
        this.inetSocketAddress = socketAddress;
        this.outcomingActions = outcomingFileSystemActions;

        // client
        this.clientOptions = new NetClientOptions().setConnectTimeout(10000).setReconnectAttempts(10).setReconnectInterval(500).setLogActivity(true);
       // this.clients.add(Vertx.vertx().createNetClient(this.clientOptions));
        this.client = Vertx.vertx().createNetClient(this.clientOptions);

    }

    @Override
    public void start(Future<Void> startFuture){
        // reading data from socket
        this.server.connectHandler(socket ->{
           // filter received message and pass to event bus
            socket.handler(buffer->{
                // decode buffer

                // if it's create - save file in path
                // if it's delete - delete local path
                // if it's modify - replace local path


                logger.info(buffer.toString());
            });
        });

        // writing data to socket
        this.server.connectHandler(socket ->{
            this.consumer = Vertx.vertx().eventBus().consumer(this.outcomingActions.toString());
            this.consumer.handler(message->{

                logger.info(String.format("Ready to write to socket %s", message.body()));
                // decode message
                // if it's create - send File
                // if it's delete - send the relative path to delete
                // if it's modify - send the file

                //socket.sendFile(message.toString());

                //socket.write(message.toString());
            });

        });
        this.server.listen();


        // client part that connect to the server (other peer)
//        for (NetClient client : this.clients){
//            client.connect(2019,"localhost", res->{
//                if (res.succeeded()){
//                    logger.info("connected to server (localhost)");
//                }
//                else{
//                    logger.info("Failed to connect the server (localhost");
//                }
//            });
//        }
            this.client.connect(4321,"localhost", res->{
                if (res.succeeded()){
                    logger.info("connected to server (localhost)");
                }
                else{
                    logger.info("Failed to connect the server (localhost");
                }
            });


        logger.info(String.format("%s opened TCP connection on port %d",this.inetSocketAddress.getHostName(),this.serverOptions.getPort()));
    }

    @Override
    public void stop(){

    }
}
