package unimelb.bitbox.actions;

import java.net.Socket;
import unimelb.bitbox.util.FileSystemManager.FileDescriptor;

public class FileDeleteResponse implements Action {

    private Socket socket;
    private static final String command = "FILE_DELETE_RESPONSE";
    private String pathName;

    public FileDeleteResponse(Socket socket, String pathName) {
        this.socket = socket;
        this.pathName = pathName;
    }

    @Override
    public void execute() {

    }

    @Override
    public int compare(Action action) {
        return 0;
    }

    @Override
    public void send() {

    }

}