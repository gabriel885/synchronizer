package synchronizer.verticles.p2p;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;

import io.vertx.core.streams.WriteStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.app.StorageApplication;
import synchronizer.models.EventBusAddress;

import java.net.InetSocketAddress;
import java.nio.Buffer;

public class TCPServerVerticle extends AbstractVerticle{

    // logger
    private static final Logger logger = LogManager.getLogger(TCPServerVerticle.class);

    // TCP Server
    private NetServer server;

    // TCP Server options (port and host)
    private NetServerOptions serverOptions;

    // Consume file system actions from event bus
    private MessageConsumer<JsonObject> consumer;

    // Consume fil system actions from storage application
    private EventBusAddress outcomingActions;


    public TCPServerVerticle(InetSocketAddress socketAddress, EventBusAddress outcomingFileSystemActions){
        this.serverOptions = new NetServerOptions().setPort(socketAddress.getPort());
        this.server = Vertx.vertx().createNetServer();
        this.outcomingActions = outcomingFileSystemActions;
    }

    @Override
    public void start(Future<Void> startFuture){
        // reading data from socket
        this.server.connectHandler(socket ->{
           // filter received message and pass to event bus
            socket.handler(buffer->{
                // decode buffer
                logger.info(buffer.toString());
            });
        });

        // writing data to socket
        this.server.connectHandler(socket ->{
            this.consumer = Vertx.vertx().eventBus().consumer(this.outcomingActions.toString());
            this.consumer.handler(message->{
                // decode message
                // if it's a created file, than send it the socket

                //socket.sendFile(message.toString());

                socket.write(message.toString());
            });

        });
        this.server.listen();
        logger.info(String.format("TCPServer is listening on port %d",this.serverOptions.getPort()));
    }

    @Override
    public void stop(){

    }
}
