package unimelb.bitbox.actions;

import java.net.Socket;

public class HandshakeResponse implements Action {

    private Socket socket;
    private static final String command = "HANDSHAKE_RESPONSE";

    public HandshakeResponse(Socket socket) {
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