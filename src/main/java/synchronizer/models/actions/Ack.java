package synchronizer.models.actions;


import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

// acknowledgement action
public class Ack extends Action {

    // the ack may have payload
    private JsonObject actionObject;

    public Ack() {
        super(ActionType.ACK);
    }

    public Ack(JsonObject actionObject){
        this.actionObject = actionObject;
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
