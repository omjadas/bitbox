package unimelb.bitbox.actions;

import java.net.Socket;

public class DirectoryDeleteResponse implements Action {

    private static final String command = "DIRECTORY_DELETE_RESPONSE";
    private String pathName;

    public DirectoryDeleteResponse(String pathName) {
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