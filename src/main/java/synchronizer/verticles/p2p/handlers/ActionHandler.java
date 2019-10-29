package synchronizer.verticles.p2p.handlers;

import io.vertx.core.Handler;
import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface ActionHandler extends Handler<NetSocket> {

    @Override
    void handle(NetSocket event);
}
