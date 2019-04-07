package unimelb.bitbox.actions;

import java.net.Socket;

public class HandshakeRequest implements Action {

    private Socket socket;
    private static final String command = "HANDSHAKE_REQUEST";
    private String host;
    private int port;

    public HandshakeRequest(Socket socket, String host, int port) {
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