package synchronizer.verticles.p2p.handlers;

import io.vertx.core.Handler;

// sockets receive actions!
public interface ActionHandler<T> extends Handler<T>{

    int connectionRetry = 5;

    @Override
    void handle(T event);

    @Override
    String toString();

}
