package unimelb.bitbox.actions;

import java.util.Date;

import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.GenericSocket;

public class HandshakeRequest implements Action {

    private GenericSocket socket;
    private static final String command = "HANDSHAKE_REQUEST";
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

    public HandshakeRequest(GenericSocket socket, String host, long port, RemotePeer remotePeer) {
        this.socket = socket;
        this.host = host;
        this.port = port;
        this.remotePeer = remotePeer;
    }

    public HandshakeRequest(GenericSocket socket, Document message, RemotePeer remotePeer) {
        this.socket = socket;

        String peerHost = ((Document) message.get("hostPort")).getString("host");
        long peerPort = ((Document) message.get("hostPort")).getLong("port");

        this.remotePeer = remotePeer;
        remotePeer.setHost(peerHost);
        remotePeer.setPort(peerPort);
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        Action response;
        response = new HandshakeResponse(socket, Configuration.getConfigurationValue("advertisedName"),
                Long.parseLong(Configuration.getConfigurationValue("port")), this.remotePeer);
        response.send();
        remotePeer.establishConnection();
    }

    @Override
    public boolean compare(Document message) {
        return message.getString("command").equals("HANDSHAKE_RESPONSE")
                || message.getString("command").equals("CONNECTION_REFUSED");
    }

    @Override
    public void send() {
        this.sendTime = (new Date()).getTime();
        this.attempts += 1;
        socket.send(toJSON());
        log.info("Sent to " + this.remotePeer.getHost() + ":" + this.remotePeer.getPort() + ": " + toJSON());
        this.remotePeer.addToWaitingActions(this);
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