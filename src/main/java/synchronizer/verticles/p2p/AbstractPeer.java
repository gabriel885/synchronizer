package synchronizer.verticles.p2p;

import io.vertx.core.*;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * represent AbstractPeer in a P2P network
 */
public abstract class AbstractPeer extends AbstractVerticle{

    // logger
    private static final Logger logger = LogManager.getLogger(AbstractPeer.class);


    // TODO: WHAT TO DO WITH THIS??
    // InetAddress
    protected InetSocketAddress inetSocketAddress;

    // Consume file system actions from event bus
    private MessageConsumer<JsonObject> consumer;

    // Produce incoming actions
    private MessageConsumer<JsonObject> producer;

    // AbstractPeer configurations
    private PeerConfig config;

    public AbstractPeer(){

    }

    /**
     * Peer configurations
     * @param config
     */
    public AbstractPeer(PeerConfig config){
        this.config = new PeerConfig();
    }

    /**
     * validate host and port
     * @param string
     * @return
     */
    public boolean validateHostAndPort(String string){

        try {
            // WORKAROUND: add any scheme to make the resulting URI valid.
            URI uri = new URI("my://" + string); // may throw URISyntaxException
            String host = uri.getHost();
            int port = uri.getPort();

            if (uri.getHost() == null || uri.getPort() == -1) {
                throw new URISyntaxException(uri.toString(),
                        "URI must have host and port parts");
            }
            return true;

        } catch (URISyntaxException ex) {
            // validation failed
            return false;
        }
    }
}
