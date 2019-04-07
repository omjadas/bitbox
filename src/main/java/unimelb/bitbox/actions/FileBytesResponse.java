package unimelb.bitbox.actions;

import java.net.Socket;
import unimelb.bitbox.util.FileSystemManager.FileDescriptor;

public class FileBytesResponse implements Action {

    private Socket socket;
    private static final String command = "FILE_BYTES_RESPONSE";
    private String pathName;

    public FileBytesResponse(Socket socket, String pathName, int position, int length, String content, String message, Boolean status) {
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