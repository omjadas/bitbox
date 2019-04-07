package unimelb.bitbox.actions;

import java.net.Socket;

public class InvalidProtocol implements Action {

    private Socket socket;
    private static final String command = "INVALID_PROTOCOL";

    public InvalidProtocol(Socket socket) {
        this.socket = socket;
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