package unimelb.bitbox.actions;

import java.net.Socket;
import unimelb.bitbox.util.FileSystemManager.FileDescriptor;

public class FileCreateResponse implements Action {

    private Socket socket;
    private static final String command = "FILE_CREATE_RESPONSE";
    private FileDescriptor fileDescriptor;
    private String pathName;

    public FileCreateResponse(Socket socket, FileDescriptor fileDescriptor, String pathName) {
        this.socket = socket;
        this.fileDescriptor = fileDescriptor;
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