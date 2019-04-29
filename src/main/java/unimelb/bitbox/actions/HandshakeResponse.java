package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Logger;

import unimelb.bitbox.Client;
import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;

public class HandshakeResponse implements Action {

    private Socket socket;
    private static final String command = "HANDSHAKE_RESPONSE";
    private Client client;

    public HandshakeResponse(Socket socket) {
        this.socket = socket;
    }

    public HandshakeResponse(Socket socket, Document message, Client client) {
        this.socket = socket;
        this.client = client;
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
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