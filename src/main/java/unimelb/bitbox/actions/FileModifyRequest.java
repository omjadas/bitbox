package unimelb.bitbox.actions;

import java.net.Socket;

public class FileModifyRequest implements Action {

    private static String command = "FILE_MODIFY_REQUEST";
    private String pathName;

    public FileModifyRequest(String pathName) {
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