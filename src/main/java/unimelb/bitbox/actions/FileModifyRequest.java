package unimelb.bitbox.actions;

import java.net.Socket;

public class FileModifyRequest implements Action {

    private Socket socket;
    private static String command = "FILE_MODIFY_REQUEST";
    private String pathName;

    public FileModifyRequest(Socket socket, String pathName) {
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