package synchronizer.models.actions;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * File System action type
 */
public abstract class Action extends JsonObject {

    public ActionType type;

    public Action(){

    }

    public Action(ActionType actionType){
        this.type = actionType;
    }

    // make action a buffer
    public abstract Buffer bufferize();

    // action as json string
    public abstract String toJson();

}
