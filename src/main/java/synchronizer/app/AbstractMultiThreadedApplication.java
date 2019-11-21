package synchronizer.app;

import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.tasks.Task;

import java.net.InetAddress;
import java.util.concurrent.*;


/**
 * Multithreaded application
 */
public abstract class AbstractMultiThreadedApplication {

    // vertx instance. Must be static otherwise application will not be able to communicate!!
    protected final static Vertx vertx = Vertx.vertx();
    // logger
    private static final Logger logger = LogManager.getLogger(AbstractMultiThreadedApplication.class);
    // local machine ip address
    protected final static String myIpAddress = getIpAddress();

    // tasks the application can spawn that will run asynchronically
    protected final ExecutorService stachosticTasks;

    // tasks are guaranteed to execute sequentially, synchronically
    // no more than one task will be active at any given time.
    protected final ExecutorService sequentTasks;


    public AbstractMultiThreadedApplication() {
        this(TaskMeta.maxPoolSize);
    }


    /**
     * @param nThreads - max number of threads allowed in thread pool
     */
    public AbstractMultiThreadedApplication(int nThreads) {
        this.stachosticTasks = new ThreadPoolExecutor(nThreads, TaskMeta.maxPoolSize, TaskMeta.keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
        this.sequentTasks = Executors.newSingleThreadExecutor();
    }

    /**
     * @return hosts IP address
     * NOTE: this is a blocking operation, therefore it's expensive and should be used as less as possible!
     */
    private static String getIpAddress() {
        // avoid redundant calls
        if (myIpAddress != null) {
            return myIpAddress;
        }

        String myIp = "";

        try {
            myIp = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            logger.error("Failed to retrieve local ip address. ");
            logger.error(e);
        }
        return myIp;
    }


    /**
     * Add task that to execute. Execution may be non-sequential
     *
     * @param task - task to schedule
     */
    protected final void scheduleStachosticTask(Task task) {
        stachosticTasks.submit(task);
    }

    /**
     * //TODO: causes blocking operation!! Add timeout to each task!
     * Add task that must be executed sequentially
     *
     * @param task - task to schedule
     */
    protected synchronized final Future scheduleSequentTask(Task task) {
        return sequentTasks.submit(task);
    }

    /**
     * start multi-threaded application
     */
    public abstract void start() ;

    /**
     * Kill all resources the application may use such as: executors tasks, vertx instances
     * event bus addresses and shared data maps
     */
    public abstract void kill();

    @Override
    public String toString() {
        return String.format("IP: %s, ", getIpAddress());
    }

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

}
