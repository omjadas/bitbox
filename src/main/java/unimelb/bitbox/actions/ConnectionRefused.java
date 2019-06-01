package unimelb.bitbox.actions;

import java.util.ArrayList;
import java.util.Date;

import unimelb.bitbox.Peer;
import unimelb.bitbox.PeerSearcher;
import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.GenericSocket;
import unimelb.bitbox.util.HostPort;

public class ConnectionRefused implements Action {

    private GenericSocket socket;
    private static final String command = "CONNECTION_REFUSED";
    private String message;
    private Document parsedJSON;
    private RemotePeer remotePeer;
    private long sendTime;
    private int attempts = 0;
    
    public long getSendTime() {
        return sendTime;
    }
    
    public int getAttempts() {
        return attempts;
    }
    
    public ConnectionRefused(GenericSocket socket, String message, RemotePeer remotePeer) {
        this.remotePeer = remotePeer;
        this.socket = socket;
        this.message = message;
    }

    public ConnectionRefused(GenericSocket socket, Document message, RemotePeer remotePeer) {
        this.remotePeer = remotePeer;
        this.socket = socket;
        this.message = message.getString("message");
        this.parsedJSON = message;
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        ArrayList<Document> hostPorts = (ArrayList<Document>) this.parsedJSON.get("peers");

        for (Document hostPort : hostPorts) {
            String host = hostPort.getString("host");
            long port = hostPort.getLong("port");

            HostPort peerHostPort = new HostPort(host, (int) port);
            PeerSearcher.potentialPeers.add(peerHostPort);
            synchronized (Peer.getPeerSearchLock()) {
                Peer.getPeerSearchLock().notifyAll();
            }
        }

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
        ArrayList<Document> peers = new ArrayList<Document>();

        for (RemotePeer remotePeer : RemotePeer.establishedPeers) {
            Document peer = new Document();
            peer.append("host", remotePeer.getHost());
            peer.append("port", remotePeer.getPort());

            peers.add(peer);
        }

        message.append("command", command);
        message.append("message", this.message);
        message.append("peers", peers);

        return message.toJson();
    }

}