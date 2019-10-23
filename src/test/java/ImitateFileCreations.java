import synchronizer.tasks.Task;
import synchronizer.utils.RandomString;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Imitate new files creation
 * Usage - new ImitateFileCreations();
 * Used for initial file creation for testing
 */
public class ImitateFileCreations extends Task {

    private static final int MAX_DELAY = 4; // maximum file creation delay
    private static final int MIN_DELAY = 2; // minimum file creation delay

    private int numFilesToCreate=0;
    // either give a random name or
    private boolean randomName = false;

    private String[] filenames;
    private Random randomSleep = new Random();

    private Path path;

    /**
     * Spawn file creation threads. Sequent execution is NOT guaranteed
     * In order to guarantee sequent execution, must be run in SequentServices pool.
     * File name will be chosen randomly
     * @param numFilesToCreate
     */
    public ImitateFileCreations(Path path, int numFilesToCreate){
        this.path = path;
        this.numFilesToCreate = numFilesToCreate;
        this.randomName = true;
    }

    /**
     * Spawn file creation threads. Sequent execution is NOT guaranteed
     * In order to guarantee sequent execution, must be run in SequentServices pool.
     * @param filenames - filenames to create
     */
    public ImitateFileCreations(Path path, String[] filenames){
        this.path = path;
        this.numFilesToCreate = filenames.length;
        this.randomName = false;
        this.filenames = wrapWithPath(filenames);
    }

    public void run() {

        new Thread(() -> {
            for (int i=0;i<numFilesToCreate;i++){
                String newFilePath = "";
                if (randomName){
                    RandomString genRandomString = new RandomString();
                    newFilePath = Paths.get(path.toString(),genRandomString.nextString() + ".txt").toString();
                }
                else{
                    newFilePath = filenames[i];
                }
                System.out.println(String.format("Creating file %s", newFilePath));
                File file = new File(newFilePath);

                try {
                    file.createNewFile();
                    TimeUnit.SECONDS.sleep(randomSleep.nextInt(MAX_DELAY)+MIN_DELAY);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }).start();
    }

    /**
     * Wrap filenames with the object's path
     * @param filenames
     * @return
     */
    public String[] wrapWithPath(String[] filenames){
        for(int i=0;i<filenames.length;i++){
            filenames[i] = Paths.get(this.path.toString(),filenames[i]).toString();
        }
        return filenames;
    }
}