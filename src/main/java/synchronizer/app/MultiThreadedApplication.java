package synchronizer.app;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import synchronizer.services.Task;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.*;


/**
 * Multithreaded application
 */
// TODO: check if generics is required here or not
public abstract class MultiThreadedApplication {

    protected ServerSocket listener;
    protected InetAddress ipAddr;

    // tasks the application can spawn that will run asynchronically
    protected final ExecutorService stachosticTasks;

    // Tasks are guaranteed to execute sequentially, synchronically
    // no more than one task will be active at any given time.
    protected final ExecutorService sequentTasks;

    // vertx instance
    protected final Vertx vertx = Vertx.vertx();

    // vertx event bus
    protected final EventBus eb = vertx.eventBus();

    // ExecutorService meta information
    class TaskMeta {
        static final int maxPoolSize = 10; // the maximum number of threads to allow in the pool

        // the number of threads to keep in the pool, even
        // if they are idle, unless {@code allowCoreThreadTimeOut} is set
        static final int corePoolSize = 2;

        // when the number of threads is greater than
        // the core, this is the maximum time that excess idle threads
        // will wait for new verticles before terminating.
        static final long keepAliveTime = 0L;
    }

    // repeated verticles
    protected List<TimerTask> repeatedTasks;

    public MultiThreadedApplication(){
        this(TaskMeta.maxPoolSize);
    }

    /**
     * @param nThreads - max number of threads allowed in thread pool
     */
    public MultiThreadedApplication(int nThreads){
        stachosticTasks = new ThreadPoolExecutor(nThreads, TaskMeta.maxPoolSize, TaskMeta.keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
        sequentTasks = Executors.newSingleThreadExecutor();
    }

    /**
     * Start application with flag arguments
     * @param args
     */
    protected abstract void start(String[] args) throws Throwable;


    /**
     * Add task that to execute. Execution may be non-sequential
     * @param task
     */
    protected final void scheduleStachosticTask(Task task){
        stachosticTasks.submit(task);
    }

    /** //TODO: causes blocking operation!! Add timeout to each task!
     * Add task that must be executed sequentially
     * @param task
     */
    protected synchronized final Future<Void> scheduleSequentTask(Task task){
        Future f = sequentTasks.submit(task);
        return f;
    }

    /**
     * Kill all executors tasks, vertx instances, event bus addresses and shared data
     * the application may use
     * @throws IOException
     */
    protected abstract void kill();

    /**
     * @return hosts IP address
     */
    protected String getIpAddress(){
        String myIp = new String();
        try{
            myIp = this.ipAddr.getLocalHost().getHostAddress();
        } catch(Exception e){
            System.out.println(e);
        }
        return myIp;
    }


    @Override
    public String toString(){
        return String.format("IP: %s, ", this.getIpAddress());
    }

}
