package synchronizer.models.actions;

import io.vertx.core.buffer.Buffer;

public class Nack extends Action {

    public Nack() {
        super(ActionType.NACK);
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
