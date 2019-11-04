package synchronizer.verticles.p2p.handlers;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// sockets receive actions!
public interface ActionHandler<T> extends Handler<T>{

    @Override
    void handle(T event);

    @Override
    String toString();

}
