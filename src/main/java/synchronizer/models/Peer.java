package synchronizer.models;

// represent peer model
public class Peer {
    private String host;
    private int port;

    public Peer(String host, int port){
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString(){
        return String.format("%s:%d", this.host, this.port);
    }
}
