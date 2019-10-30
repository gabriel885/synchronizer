package synchronizer.verticles.p2p.handlers;

import synchronizer.verticles.p2p.handlers.ActionHandler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// client handlers
public class ClientHandlers {

    private static Set<ActionHandler> handlers = new HashSet<>();

    public ClientHandlers(ActionHandler handler){
        handlers.add(handler);
    }

    /**
     * get registered server handlers
     * @return
     */
    static Set<ActionHandler> getHandlers(){
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
