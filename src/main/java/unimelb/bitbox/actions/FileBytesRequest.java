package unimelb.bitbox.actions;

import java.net.Socket;
import unimelb.bitbox.util.FileSystemManager.FileDescriptor;

public class FileBytesRequest implements Action {

    private Socket socket;
    private static final String command = "FILE_BYTES_REQUEST";
    private String pathName;
    private int position;
    private int length;

    public FileBytesRequest(Socket socket, String pathName, int position, int length) {
        this.socket = socket;
        this.pathName = pathName;
        this.position = position;
        this.length = length;
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