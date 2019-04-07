package unimelb.bitbox.actions;

import java.net.Socket;

public class ConnectionRefused implements Action {

    private static final String command = "CONNECTION_REFUSED";

    public ConnectionRefused(Socket socket) {

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