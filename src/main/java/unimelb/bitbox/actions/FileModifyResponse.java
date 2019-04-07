package unimelb.bitbox.actions;

import java.net.Socket;

public class FileModifyResponse implements Action {

    private Socket socket;
    private static final String command = "FILE_MODIFY_RESPONSE";
    private String pathName;

    public FileModifyResponse(Socket socket, String pathName) {
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