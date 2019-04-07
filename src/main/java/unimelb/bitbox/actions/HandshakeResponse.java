package unimelb.bitbox.actions;

import java.net.Socket;

public class HandshakeResponse implements Action {

    private Socket socket;
    private static final String command = "HANDSHAKE_RESPONSE";
    private String host;
    private int port;

    public HandshakeResponse(Socket socket, String host, int port) {
        this.socket = socket;
        this.host = host;
        this.port = port;
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