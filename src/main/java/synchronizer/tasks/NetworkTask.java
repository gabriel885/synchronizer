package synchronizer.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

abstract class NetworkTask extends Task {

    protected DataInputStream inputStream;
    protected DataOutputStream outputStream;
    protected Socket socket;


}
