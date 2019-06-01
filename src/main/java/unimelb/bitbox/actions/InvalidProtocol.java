package unimelb.bitbox.actions;

import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.GenericSocket;

public class InvalidProtocol implements Action {

    private GenericSocket socket;
    private static final String command = "INVALID_PROTOCOL";
    private String message;
    private RemotePeer remotePeer;

    public InvalidProtocol(GenericSocket socket, String message, RemotePeer remotePeer) {
        this.socket = socket;
        this.message = message;
        this.remotePeer = remotePeer;
    }

    public InvalidProtocol(GenericSocket socket, Document message, RemotePeer remotePeer) {
        this.socket = socket;
        this.message = message.getString("message");
        this.remotePeer = remotePeer;
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        socket.disconnect(remotePeer);
    }

    @Override
    public boolean compare(Document action) {
        return true;
    }

    @Override
    public void send() {
        socket.send(toJSON());
        log.info("Sent to " + this.remotePeer.getHost() + ":" + this.remotePeer.getPort() + ": " + toJSON());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        socket.disconnect(remotePeer);
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