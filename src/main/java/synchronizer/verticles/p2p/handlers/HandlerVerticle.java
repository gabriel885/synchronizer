package synchronizer.verticles.p2p.handlers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.NetSocket;

// a handler that is also vertx deployable
public abstract class HandlerVerticle<T> extends AbstractVerticle implements ActionHandler<T> {

    // start verticle
    public abstract void start();

    // stop verticle
    public abstract void stop();

    public abstract void handle(T event);

}
