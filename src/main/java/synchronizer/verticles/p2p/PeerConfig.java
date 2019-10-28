package synchronizer.verticles.p2p;

import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServerOptions;

import java.util.concurrent.TimeUnit;

// AbstractPeer configuration
// Immutable type
public class PeerConfig {

    // at least one peer should be in the network
    public static final int DEFAULT_MIN_NUMBER_OF_ACTIVE_CONNECTIONS = 1;

    public static final int DEFAULT_MAX_READ_IDLE_SECONDS = 120;

    // hearbeat all peers
    public static final int DEFAULT_KEEP_ALIVE_SECONDS = 15;

    public static final int DEFAULT_PING_TIMEOUT_SECONDS = 5;

    // how many times try to connect to a peer
    public static final int DEFAULT_RETRY_PING_TIMES = 5;



    public static final int DEFAULT_PING_TTL = 7;


    public PeerConfig(){

    }
    /**
     * Name of the peer. It must be unique across the p2p network
     */
    private String peerName;

    /**
     * When a peer has less number of connections than min number of active connections,
     * it pings other peers periodically and try to connect to new peers
     */
    private int minNumberOfActiveConnections = DEFAULT_MIN_NUMBER_OF_ACTIVE_CONNECTIONS;

    private int retryPingTimes = DEFAULT_RETRY_PING_TIMES;
    /**
     * When a peer does not send any message for the specified amount of seconds, it will be disconnected
     */
    private int maxReadIdleSeconds = DEFAULT_MAX_READ_IDLE_SECONDS;

    /**
     * Amount of time that a peer will send periodic keep-alive messages to its neighbours to indicate that it is alive
     */
    private int keepAlivePeriodSeconds = DEFAULT_KEEP_ALIVE_SECONDS;

    /**
     * Amount of seconds that pong responses will be waited for an initiated ping
     */
    private int pingTimeoutSeconds = DEFAULT_PING_TIMEOUT_SECONDS;

    /**
     * Amount of neighbour jumps an initial ping message will do
     */
    private int pingTTL = DEFAULT_PING_TTL;


    // Getters

    public String getPeerName() {
        return peerName;
    }

    public int getMinNumberOfActiveConnections() {
        return minNumberOfActiveConnections;
    }

    public int getMaxReadIdleSeconds() {
        return maxReadIdleSeconds;
    }

    public int getKeepAlivePeriodSeconds() {
        return keepAlivePeriodSeconds;
    }

    public int getPingTimeoutSeconds() {
        return pingTimeoutSeconds;
    }

    public long getPingTimeoutMillis() {
        return TimeUnit.SECONDS.toMillis(pingTimeoutSeconds);
    }

    public int getPingTTL() {
        return pingTTL;
    }



    @Override
    public String toString() {

        return String.format("PeerConfig{peerName:%s, keepAlivePeriodSeconds:%s, pingTimeoutSeconds:%s," +
                        "pingTTL:%s}",
                this.peerName,
                this.keepAlivePeriodSeconds,
                this.pingTimeoutSeconds,
                this.pingTTL
                );

    }
}