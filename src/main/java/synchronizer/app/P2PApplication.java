package synchronizer.app;

import java.io.IOException;


public class P2PApplication extends MultiThreadedApplication {

   // NetServer netServer = vertx.createNetServer();

 //   Future<NetServer> netServerFuture = Future.future(promise -> netServer.listen(promise));


    @Override
    public void start(String[] args) throws IOException {

    }

    @Override
    public void kill() {
        System.out.println("P2PApplication is shutting down...");
        vertx.close();
    }


}
