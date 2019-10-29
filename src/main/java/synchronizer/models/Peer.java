package synchronizer.models;

// represent peer model
public class Peer implements Comparable<Peer>{
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


    public String getHost(){
        return this.host;
    }

    public int getPort(){
        return this.port;
    }

    @Override
    public int compareTo(Peer o) {
        if (o.host.equals(this.host) && o.port == this.port){
            return 1;
        }
        return 0;
    }
}
