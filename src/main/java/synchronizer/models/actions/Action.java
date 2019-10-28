package synchronizer.models.actions;

/**
 * File System action type
 */
public abstract class Action {
    protected final ActionType actionType;

    public Action(ActionType actionType){
        this.actionType = actionType;
    }

}
