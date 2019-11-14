package synchronizer.tasks;

/**
 * Context carries deadlines, cancellation signals, and other request-scoped values across Thread boundary.
 * Must be passed to Runnable's only!!
 */
public final class Context implements Runnable{

    // parent conetxt
    protected final Context parentContext;

    /**
     * cancel duration of Context time in milliseconds
     */
    protected final long cancelDuration;


    /**
     * Create context without a parent context and without cancelDuration timeout
     */
    public Context(){
        this(-1);
    }

    /**
     * Create a context without a parent with cancelDuration timeout
     * @param timeout
     */
    public Context(long timeout){
        parentContext=null;
        cancelDuration=timeout;
    }

    /**
     * Create context with parent context and without cancelDuration timeout
     * @param parentContext
     */
    public Context(Context parentContext){

        this(parentContext,-1);
    }

    /**
     * Create context with parent context with cancelDuration timeout
     * @param parentContext
     * @param timeout
     */
    public Context(Context parentContext, long timeout){
        this.parentContext = parentContext;
        this.cancelDuration = timeout;
    }

    /**
     * Context cancelDuration countdown
     */
    @Override
    public void run(){
        try{
            Thread.sleep(cancelDuration);
        } catch (InterruptedException e){
            System.out.println(String.format("Context duration interrupted. Error %s", e.getMessage()));
        }
        // wake up the only service that listens to context and terminate
        notifyAll();
    }

}
