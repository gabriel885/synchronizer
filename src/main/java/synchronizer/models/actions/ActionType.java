package synchronizer.models.actions;

public enum ActionType {
    ACK,
    CREATE,
    RENAME,
    DELETE,
    MODIFY;
    public static ActionType getType(String action){
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
                    return null; // unknown action type
        }
    }
}
