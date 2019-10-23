package synchronizer.app;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import synchronizer.tasks.Task;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.*;


/**
 * Multithreaded application
 */
public abstract class MultiThreadedApplication {

    // tasks the application can spawn that will run asynchronically
    protected final ExecutorService stachosticTasks;

    // tasks are guaranteed to execute sequentially, synchronically
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

    // repeated tasks
    protected List<TimerTask> repeatedTasks;

    public MultiThreadedApplication() {
        this(TaskMeta.maxPoolSize);

    }

    /**
     * @param nThreads - max number of threads allowed in thread pool
     */
    public MultiThreadedApplication(int nThreads){
        this.stachosticTasks = new ThreadPoolExecutor(nThreads, TaskMeta.maxPoolSize, TaskMeta.keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
        this.sequentTasks = Executors.newSingleThreadExecutor();
    }

    /**
     * Start application with flag arguments
     * @param args
     */
    public abstract void start(String[] args) throws Exception;


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
     * Kill all resources the application may use such as: executors tasks, vertx instances
     * event bus addresses and shared data maps
     */
    public abstract void kill();

    /**
     * @return hosts IP address
     */
    protected String getIpAddress(){
        InetAddress ipAddr;
        try{
            ipAddr =  InetAddress.getLocalHost();
        }catch (UnknownHostException e){
            System.out.println(e.getMessage());
            return "";
        }

        String myIp = new String();
        try{
            myIp = ipAddr.getLocalHost().getHostAddress();
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
