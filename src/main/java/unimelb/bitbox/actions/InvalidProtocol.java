package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;

public class InvalidProtocol implements Action {

    private Socket socket;
    private static final String command = "INVALID_PROTOCOL";
    private String message;
    private RemotePeer remotePeer;

    public InvalidProtocol(Socket socket, String message, RemotePeer remotePeer) {
        this.socket = socket;
        this.message = message;
        this.remotePeer = remotePeer;
    }

    public InvalidProtocol(Socket socket, Document message, RemotePeer remotePeer) {
        this.socket = socket;
        this.message = message.getString("message");
        this.remotePeer = remotePeer;
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
            log.info("Sent to " + this.remotePeer.getHost() + ":" + this.remotePeer.getPort() + ": " + toJSON());
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
    private String toJSON() {
        Document message = new Document();

        message.append("command", command);
        message.append("message", this.message);

        return message.toJson();
    }

}