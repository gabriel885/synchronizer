package synchronizer;

import org.apache.commons.io.monitor.FileAlterationObserver;
import synchronizer.app.P2PApplication;
import synchronizer.app.StorageApplication;
import synchronizer.exceptions.ApplicationFailure;

import java.io.File;

public class Main {

    public static void main(String [] args) throws ApplicationFailure {

        // initialize storage application
        final StorageApplication storageApplication = new StorageApplication();

        // initialize p2p application
        final P2PApplication p2p = new P2PApplication();


        try{
            storageApplication.start(args);
            p2p.start(args);

        } catch(Exception e){
            storageApplication.kill();
            p2p.kill();
            throw new ApplicationFailure(e);
        }




        try {
            // 100 seconds
            Thread.sleep(100000);
        }catch (Exception e){

        }

        storageApplication.kill();
        p2p.kill();

        System.out.println("Finished running application");

    }
}
