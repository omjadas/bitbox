package unimelb.bitbox.actions;

import java.net.Socket;

public class ConnectionRefused implements Action {

    private Socket socket;
    private static final String command = "CONNECTION_REFUSED";
    private String message;

    public ConnectionRefused(Socket socket, String message) {
        this.socket = socket;
        this.message = message;
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