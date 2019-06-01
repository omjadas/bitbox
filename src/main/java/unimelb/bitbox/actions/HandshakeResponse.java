package unimelb.bitbox.actions;

import java.util.Date;

import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.GenericSocket;

public class HandshakeResponse implements Action {

    private GenericSocket socket;
    private static final String command = "HANDSHAKE_RESPONSE";
    private String host;
    private long port;
    private RemotePeer remotePeer;
    private long sendTime;
    private int attempts = 0;
    
    public long getSendTime() {
        return sendTime;
    }
    
    public int getAttempts() {
        return attempts;
    }

    public HandshakeResponse(GenericSocket socket, String host, long port, RemotePeer remotePeer) {
        this.socket = socket;
        this.host = host;
        this.port = port;
        this.remotePeer = remotePeer;
    }

    public HandshakeResponse(GenericSocket socket, Document message, RemotePeer remotePeer) {
        this.socket = socket;

        String peerHost = ((Document) message.get("hostPort")).getString("host");
        long peerPort = ((Document) message.get("hostPort")).getLong("port");

        this.host = peerHost;
        this.port = peerPort;

        this.remotePeer = remotePeer;
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        remotePeer.establishConnection();
    }

    @Override
    public boolean compare(Document action) {
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
        Document hostPort = new Document();

        hostPort.append("host", host);
        hostPort.append("port", port);

        message.append("command", command);
        message.append("hostPort", hostPort);

        return message.toJson();
    }

}