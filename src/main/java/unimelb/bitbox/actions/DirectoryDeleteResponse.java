package unimelb.bitbox.actions;

import java.util.Date;

import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.GenericSocket;

public class DirectoryDeleteResponse implements Action {

    private GenericSocket socket;
    private static final String command = "DIRECTORY_DELETE_RESPONSE";
    private String pathName;
    private String message;
    private Boolean status;
    private RemotePeer remotePeer;
    private long sendTime;
    private int attempts = 0;
    
    public long getSendTime() {
        return sendTime;
    }
    
    public int getAttempts() {
        return attempts;
    }

    public DirectoryDeleteResponse(GenericSocket socket, String pathName, String message, Boolean status,
            RemotePeer remotePeer) {
        this.socket = socket;
        this.pathName = pathName;
        this.message = message;
        this.status = status;
        this.remotePeer = remotePeer;
    }

    public DirectoryDeleteResponse(GenericSocket socket, Document message, RemotePeer remotePeer) {
        this.socket = socket;
        this.pathName = message.getString("pathName");
        this.message = message.getString("message");
        this.status = message.getBoolean("status");
        this.remotePeer = remotePeer;
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
        this.sendTime = (new Date()).getTime();
        this.attempts += 1;
        socket.send(toJSON());
        log.info("Sent to " + this.remotePeer.getHost() + ":" + this.remotePeer.getPort() + ": " + toJSON());
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