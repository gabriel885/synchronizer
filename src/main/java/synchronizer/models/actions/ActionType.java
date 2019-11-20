package synchronizer.models.actions;

public enum ActionType {
    ACK, // acknowledgement
    NACK, // negative acknowledgement
    CREATE,
    DELETE,
    MODIFY,
    REQUEST, // request file
    RESPONSE, // response with a file
    UNKNOWN;


    /**
     *
     * @param type- type as string! (e.g CREATE, RESPONSE, DELETE)
     *            don't confuse with sending action strings!
     * @return
     */
    public static ActionType getType(String type) {
        if (type == null || type.equals("")) {
            return ActionType.UNKNOWN;
        }
        switch (type) {
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
            default:
                return ActionType.UNKNOWN; // unknown action type
        }
    }


    /**
     * validate action type
     *
     * @param actionType - action type as string
     * @return false if the action is unknown
     */
    public static boolean isValidType(String actionType) {
        return getType(actionType) != ActionType.UNKNOWN;
    }
}
