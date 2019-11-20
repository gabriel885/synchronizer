package synchronizer.models.actions;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

/**
 * File System action type
 */
public abstract class Action extends JsonObject {

    private ActionType type;

    Action(ActionType actionType) {
        this.type = actionType;
    }


    /**
     *
     * @param jsonAction - json action as string
     *                   Example Input:  {"type":"DELETE","path":"/opt/dir/example.txt","timestamp":1574260791,"isDir":false}
     *                   Output: ActionType.DELETE
     * @return action type
     */
    public static ActionType getActionType(JsonObject jsonAction){
        // validate json object
        if (jsonAction == null || !jsonAction.containsKey("type") || jsonAction.getString("type")==null){
            return ActionType.UNKNOWN;
        }
        return ActionType.getType(jsonAction.getString("type"));
    }
    // TODO: fix this!
    /**
     * return true if action is isValid - contains isValid json scheme according to action type
     *
     * @param jsonAction - action represented as jsonObject
     * @return true if jsonAction is isValid and false otherwise
     */
    public static boolean isValid(JsonObject jsonAction) {
        if (jsonAction==null){
            return false;
        }
        if (ActionType.isValidType(jsonAction.getString("type"))) {
            switch (Action.getActionType(jsonAction)) {
                case DELETE:
                    return true;
                case MODIFY:
                    if (!jsonAction.containsKey("path") || !jsonAction.containsKey("timestamp")){
                        return false;
                    }
                    // check if mandatory keys exist
                    if (!jsonAction.containsKey("checksum") || !jsonAction.containsKey("buffer") || !jsonAction.containsKey("isDir")){
                        return false;
                    }
                    return true;
                case CREATE:
                    if (!jsonAction.containsKey("path") || !jsonAction.containsKey("timestamp")){
                        return false;
                    }
                    // check if mandatory keys exist
                    if (!jsonAction.containsKey("checksum") || !jsonAction.containsKey("buffer") || !jsonAction.containsKey("isDir")){
                        return false;
                    }
                    return true;
                case REQUEST:
                    if (!jsonAction.containsKey("path") || !jsonAction.containsKey("timestamp")){
                        return false;
                    }
                    return true;
                case RESPONSE:
                    if (!jsonAction.containsKey("checksum") || !jsonAction.containsKey("buffer") || !jsonAction.containsKey("isDir")){
                        return false;
                    }
                    return true;
                case ACK:
                    return true;
                case NACK:
                    return true;
                case UNKNOWN:
                    return false;
                default: // non-recognizable type is not isValid
                    return false;
            }
        } else {
            return false;
        }
    }

    public ActionType getType() {
        return this.type;
    }

    // make action a buffer
    public abstract Buffer bufferize();

    // action as json string
    public abstract String toJson();

}
