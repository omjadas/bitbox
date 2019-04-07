package unimelb.bitbox.actions;

import java.net.Socket;

public class FileDeleteRequest implements Action {

    private static final String command = "FILE_DELETE_REQUEST";
    private String pathName;

    public FileDeleteRequest(String pathName) {
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