package synchronizer.services;

import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


// application service
public abstract class Service implements Runnable {

    // default service timeout in seconds
    private final int defaultTimeout = 5;

    // allowed inheritance classes
    private final ArrayList<Class> allowed = new ArrayList<Class>(){{
        // add(StorageService.class);
    }};


    private final Duration timeout = Duration.ofSeconds(defaultTimeout);

    // Services Socket
    protected Socket ipAddr;

    // Service's context
    private Context ctx;

    private final AtomicBoolean running = new AtomicBoolean(false);


    // no context declared
    public Service() {
        // TODO: Should not throw an exception. should throw future indicating an error
        //validInheritence();
        // if StorageService not superclass throw an error
        this.ctx=null;
    }

    /**
     * ensure that only Storage
     */
    private void validInheritence() throws Exception{
        for (Class c: allowed
        ) {
            if(!c.isInstance(this)){
                callFailure(new Exception(String.format("%s is not allowed to inherit Service. Only StorageService is allowed",c.getName())));
            }
        }
    }

    public final Context Service() {
        this.ctx = new Context();
        return ctx;
    }

    // //TODO: Context should kill service on failure?? and return future indicating a failure
    public final Exception callFailure(Exception e) throws Exception{
        throw e;
    }

    /**
     * Spawn service with cancelDuration timeout
     * @param timeout
     * @return
     */
    public final Context Service(long timeout){
        this.ctx = new Context(timeout);
        return ctx;
    }

    protected final void finalize()
            throws Throwable{

    }

}

