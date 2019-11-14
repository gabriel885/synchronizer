package synchronizer.tasks;

import synchronizer.app.StorageApplication;

import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


// Application's Task
public abstract class Task implements Runnable {

    // default service timeout in seconds
    int defaultTimeout = 5;

    // immutable list
    final List<Class> allowed = Collections.unmodifiableList(new ArrayList<Class>(){
        {
            add(StorageApplication.class);
        }
    });

    final Duration timeout = Duration.ofSeconds(defaultTimeout);

    // Services Socket
    Socket ipAddr;

    // Task's context
    Context ctx;

    final AtomicBoolean running = new AtomicBoolean(false);


    // no context declared
    public Task() {
        // Context factory
        this.ctx=null;
    }

    /**
     * ensure that only Storage
     */
    private void validInheritence() throws Exception{
        for (Class c: allowed
        ) {
            if(!c.isInstance(this)){
                callFailure(new Exception(String.format("%s is not allowed to inherit Task. Only StorageService is allowed",c.getName())));
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
    public final Context Task(long timeout){
        this.ctx = new Context(timeout);
        return ctx;
    }

    protected final void finalize()
            throws Throwable{
    }

}

