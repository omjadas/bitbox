package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import unimelb.bitbox.Client;
import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;

public class HandshakeRequest implements Action {

    private Socket socket;
    private static final String command = "HANDSHAKE_REQUEST";
    private Client client;

    public HandshakeRequest(Socket socket) {
        this.socket = socket;
    }

    public HandshakeRequest(Socket socket, Document message, Client client) {
        this.socket = socket;

        String clientHost = ((Document) message.get("hostPort")).getString("host");
        long clientPort = ((Document) message.get("hostPort")).getLong("port");

        this.client = client;
        client.setPort(clientPort);
        client.setHost(clientHost);
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        Action response;
        response = new HandshakeResponse(socket);
        response.send();
        client.establishConnection();
    }

    @Override
    public int compare(Action action) {
        return 0;
    }

    @Override
    public void send() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            out.write(toJSON());
            out.newLine();
            out.flush();
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

        hostPort.append("host", Configuration.getConfigurationValue("advertisedName"));
        hostPort.append("port", Integer.parseInt(Configuration.getConfigurationValue("port")));

        message.append("command", command);
        message.append("hostPort", hostPort);

        return message.toJson();
    }

}