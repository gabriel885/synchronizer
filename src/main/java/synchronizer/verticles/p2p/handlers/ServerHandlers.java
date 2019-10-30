package synchronizer.verticles.p2p.handlers;

import synchronizer.verticles.p2p.handlers.ActionHandler;

import java.util.*;

// server handlers
public class ServerHandlers {

    private static Set<ActionHandler> handlers = new HashSet<>();

    public ServerHandlers(ActionHandler handler){
        handlers.add(handler);
    }

    /**
     * get registered server handlers
     * @return
     */
    public Set<ActionHandler> getHandlers(){
        return handlers;
    }

    /**
     * return an iterator to handlers set
     * @return
     */
    public Iterator<ActionHandler> getHandlersIterator(){
        return handlers.iterator();
    }

    /**
     * add action handler to handlers
     * @param handler
     * @return
     */
    public Set<ActionHandler> add(ActionHandler handler){
        handlers.add(handler);
        return handlers;
    }
}
