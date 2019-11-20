package synchronizer.verticles.p2p.handlers;

import io.vertx.core.Handler;

// Action handler is binded to server and client sockets
// the difference between the server and the client server
// is that the client receives an AsyncResult of NetSocket - after trying to connect the server
// while the server connects immediately the socket locally
public interface ActionHandler<T> extends Handler<T> {

    @Override
    void handle(T event);

    @Override
    String toString();

}
