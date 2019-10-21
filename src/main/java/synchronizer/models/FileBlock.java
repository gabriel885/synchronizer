package synchronizer.models;

// file block
public class FileBlock implements Cloneable{


    public FileBlock(){

    }

    // deep copy FileBlock object
    public FileBlock clone(){
        FileBlock fb = new FileBlock();
        return fb;
    }

}
