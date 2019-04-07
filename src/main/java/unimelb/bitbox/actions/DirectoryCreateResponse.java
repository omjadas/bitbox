package unimelb.bitbox.actions;

import java.net.Socket;

public class DirectoryCreateResponse implements Action {

    private Socket socket;
    private static final String command = "DIRECTORY_CREATE_RESPONSE";
    private String pathName;
    private String message;
    private Boolean status;

    public DirectoryCreateResponse(Socket socket, String pathName, String message, Boolean status) {
        this.socket = socket;
        this.pathName = pathName;
        this.message = message;
        this.status = status;
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