package synchronizer.models.actions;

public enum ActionType {
    ACK, // acknowledgement
    NACK, // negative acknowledgement
    CREATE,
    DELETE,
    MODIFY,
    REQUEST, // request file
    RESPONSE, // response with a file
    SYNC,
    UNKNOWN;
    public static ActionType getType(String type){
        if (type==null || type.equals("")){
            return ActionType.UNKNOWN;
        }
        switch(type){
            case "ACK":
                return ActionType.ACK;
            case "NACK":
                return ActionType.NACK;
            case "CREATE":
                return ActionType.CREATE;
            case "DELETE":
                    return ActionType.DELETE;
            case "MODIFY":
                return ActionType.MODIFY;
            case "REQUEST":
                return ActionType.REQUEST;
            case "RESPONSE":
                return ActionType.RESPONSE;
            case "SYNC":
                return ActionType.SYNC;
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
