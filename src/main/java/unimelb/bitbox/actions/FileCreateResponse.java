package unimelb.bitbox.actions;

import java.net.Socket;

public class FileCreateResponse implements Action {

    private static final String command = "FILE_CREATE_RESPONSE";
    private String pathName;

    public FileCreateResponse(String pathName) {
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