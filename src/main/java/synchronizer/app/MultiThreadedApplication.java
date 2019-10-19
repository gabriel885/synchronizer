package synchronizer.app;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import synchronizer.services.Service;

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

    // verticles the server can spawn
    protected final ExecutorService stachosticServices;

    // Tasks are guaranteed to execute sequentially, and no more than
    // one task will be active at any given time.
    protected final ExecutorService sequentServices;

    // Vertx instance
    protected final Vertx vertx = Vertx.vertx();


    // ExecutorService meta information
    class ServiceMeta {
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
        this(ServiceMeta.maxPoolSize);
    }

    /**
     * @param nThreads - max number of threads allowed in thread pool
     */
    public MultiThreadedApplication(int nThreads){
        stachosticServices = new ThreadPoolExecutor(nThreads, ServiceMeta.maxPoolSize, ServiceMeta.keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
        sequentServices = Executors.newSingleThreadExecutor();
    }

    /**
     * Start application with flag arguments
     * @param args
     */
    protected abstract void start(String[] args) throws Throwable;


    /**
     * Add service that to execute. Execution may be non-sequential
     * @param service
     */
    protected final void addStachosticService(Service service){
        stachosticServices.submit(service);
    }

    /** //TODO: causes blocking operation!! Add timeout to each service!
     * Add service that must be executed sequentially
     * @param service
     */
    protected synchronized final Future addSequentService(Service service){
        Future f = sequentServices.submit(service);
        return f;
    }

    /**
     * Kill all executor verticles
     * @throws IOException
     */
    protected final void kill(){
        System.out.println("Killing stachistic and sequent verticles");
        stachosticServices.shutdownNow();
        sequentServices.shutdownNow();
    }


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
