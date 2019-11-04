package synchronizer.models.actions;

import org.omg.CORBA.UNKNOWN;

public enum ActionType {
    ACK, // acknowledgement
    NACK, // negative acknowledgement
    CREATE,
    DELETE,
    MODIFY,
    REQUEST, // request sending file
    UNKNOWN;
    public static ActionType getType(String action){
        if (action==null){
            return ActionType.UNKNOWN;
        }
        switch(action){
            case "ACK":
                return ActionType.ACK;
            case "CREATE":
                return ActionType.CREATE;
            case "DELETE":
                    return ActionType.DELETE;
            case "MODIFY":
                return ActionType.MODIFY;
                default:
                    return ActionType.UNKNOWN; // unknown action type
        }
    }


    /**
     * returns true if action type is valid - not unknown
     * @param actionType
     * @return
     */
    public static boolean isValidType(String actionType){
        if (getType(actionType) == ActionType.UNKNOWN){
            return false;
        }
        return true;
    }
}
