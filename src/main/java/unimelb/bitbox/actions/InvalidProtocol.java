package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import unimelb.bitbox.Client;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;

public class InvalidProtocol implements Action {

    private Socket socket;
    private static final String command = "INVALID_PROTOCOL";
    private String message;
    private Client client;

    public InvalidProtocol(Socket socket, String message, Client client) {
        this.socket = socket;
        this.message = message;
        this.client = client;
    }

    public InvalidProtocol(Socket socket, Document message, Client client) {
        this.socket = socket;
        this.message = message.getString("message");
        this.client = client;
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {

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
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            socket.close();
        } catch (IOException e) {
            log.info("Socket was closed while sending message");
        }
    }

    /**
     * Convert the action to JSON
     * 
     * @return JSON string
     */
    @Override
    public String toJSON() {
        Document message = new Document();

        message.append("command", command);
        message.append("message", this.message);

        return message.toJson();
    }

}