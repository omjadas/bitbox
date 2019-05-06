package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;

public class DirectoryDeleteResponse implements Action {

    private Socket socket;
    private static final String command = "DIRECTORY_DELETE_RESPONSE";
    private String pathName;
    private String message;
    private Boolean status;
    private RemotePeer client;

    public DirectoryDeleteResponse(Socket socket, String pathName, String message, Boolean status, RemotePeer client) {
        this.socket = socket;
        this.pathName = pathName;
        this.message = message;
        this.status = status;
        this.client = client;
    }

    public DirectoryDeleteResponse(Socket socket, Document message, RemotePeer client) {
        this.socket = socket;
        this.pathName = message.getString("pathName");
        this.message = message.getString("message");
        this.status = message.getBoolean("status");
        this.client = client;
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {

    }

    @Override
    public boolean compare(Document message) {
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

        message.append("command", command);
        message.append("pathName", pathName);
        message.append("message", this.message);
        message.append("status", status);

        return message.toJson();
    }

}