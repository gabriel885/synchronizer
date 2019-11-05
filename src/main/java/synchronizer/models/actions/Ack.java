package synchronizer.models.actions;


import io.vertx.core.buffer.Buffer;

// acknowledgement action
public class Ack extends Action {

    public Ack() {
        super(ActionType.ACK);
    }

    @Override
    public Buffer bufferize() {
        return Buffer.buffer(toJson());
    }

    @Override
    public String toJson() {
        return "{ack}";
    }

}
