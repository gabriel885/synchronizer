package synchronizer.models.actions;

import io.vertx.core.buffer.Buffer;
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

    /**
     * return true if action is valid - contains valid json scheme according to action type
     * @param jsonAction
     * @return
     */
    public static boolean valid(JsonObject jsonAction){
        if (ActionType.isValidType(jsonAction.getString("type")) && jsonAction.containsKey("path") && jsonAction.containsKey("timestamp")){
            switch(ActionType.getType(jsonAction.getString("type"))){
                case DELETE:
                    return true;
                case MODIFY:
                    if (!jsonAction.containsKey("checksum") || !jsonAction.containsKey("buffer")){
                        return false;
                    }
                    return true;
                case CREATE:
                    if (!jsonAction.containsKey("checksum") || !jsonAction.containsKey("buffer")){
                        return false;
                    }
                    return true;
                case UNKNOWN:
                    return false;
                default: // ACK NACK REQUEST RESPONSE
                    return true;
            }
        }else{
            return false;
        }
    }

}
