package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.PeerSearcher;
import unimelb.bitbox.Peer;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.HostPort;

public class ConnectionRefused implements Action {

    private Socket socket;
    private static final String command = "CONNECTION_REFUSED";
    private String message;
    private Document parsedJSON;
    private RemotePeer remotePeer;
        
    public ConnectionRefused(Socket socket, String message, RemotePeer remotePeer) {
        this.remotePeer = remotePeer;
        this.socket = socket;
        this.message = message;
    }

    public ConnectionRefused(Socket socket, Document message, RemotePeer remotePeer) {
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
            synchronized(Peer.getPeerSearchLock()) {
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