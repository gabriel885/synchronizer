package synchronizer.models.actions;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

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
        return new JsonObject().put("type","NACK").toString();
    }
}
