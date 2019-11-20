package synchronizer.verticles.p2p.handlers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// client-server collection of handlers
// TODO: is this necessary?
public class Handlers {

    private static final Set<ActionHandler> handlers = new HashSet<>();

    // must be at least one handler for client-server to connect
    public Handlers(ActionHandler handler) {
        handlers.add(handler);
    }

    /**
     * get registered server handlers
     *
     * @return a set of all registered handlers
     */
    static Set<ActionHandler> getHandlers() {
        return handlers;
    }

    /**
     * return an iterator to handlers set
     *
     * @return an iterator to handlers
     */
    public Iterator<ActionHandler> getHandlersIterator() {
        return handlers.iterator();
    }

    /**
     * add action handler to handlers
     *
     * @param handler - socket action handler
     * @return a set of all registered handlers
     */
    public Set<ActionHandler> add(ActionHandler handler) {
        handlers.add(handler);
        return handlers;
    }

}
