package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;

public class HandshakeResponse implements Action {

    private Socket socket;
    private static final String command = "HANDSHAKE_RESPONSE";
    private String host;
    private long port;
    private RemotePeer client;

    public HandshakeResponse(Socket socket, String host, long port, RemotePeer client) {
        this.socket = socket;
        this.host = host;
        this.port = port;
        this.client = client;
    }

    public HandshakeResponse(Socket socket, Document message, RemotePeer client) {
        this.socket = socket;

        String clientHost = ((Document) message.get("hostPort")).getString("host");
        long clientPort = ((Document) message.get("hostPort")).getLong("port");

        this.host = clientHost;
        this.port = clientPort;

        this.client = client;
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        client.establishConnection();
    }

    @Override
    public boolean compare(Document action) {
        return true;
    }

    @Override
    public void send() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            out.write(toJSON());
            out.newLine();
            out.flush();
            log.info("Sent to " + this.client.getHost() + ":" + this.client.getPort() + ": " + toJSON());
        } catch (IOException e) {
            log.info("Socket was closed while sending message");
        }
    }

    /**
     * Convert the action to JSON
     * 
     * @return JSON string
     */
    private String toJSON() {
        Document message = new Document();
        Document hostPort = new Document();

        hostPort.append("host", host);
        hostPort.append("port", port);

        message.append("command", command);
        message.append("hostPort", hostPort);

        return message.toJson();
    }

}