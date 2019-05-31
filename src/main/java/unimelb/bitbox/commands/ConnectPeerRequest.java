package unimelb.bitbox.commands;

import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.ServerMain;
import unimelb.bitbox.util.Document;

public class ConnectPeerRequest implements Command {
    private static final String command = "CONNECT_PEER_REQUEST";
    private String host;
    private long port;

    public ConnectPeerRequest(String host, long port) {
        this.host = host;
        this.port = port;
    }
    
    public ConnectPeerRequest(Document doc) {
        this.host = doc.getString("host");
        this.port = doc.getLong("port");
    }

    @Override
    public String execute() {
        RemotePeer peer = new RemotePeer(this.host, (int) this.port, ServerMain.fileSystemManager);
        if (peer.getIsConnected()) {
            return new ConnectPeerResponse(true, "connected to peer").getPayload();
        } else {
            return new ConnectPeerResponse(false, "connection failed").getPayload();
        }
    }

    @Override
    public String getPayload() {
        Document payload = new Document();
        payload.append("command", command);
        payload.append("host", this.host);
        payload.append("port", this.port);
        return payload.toJson();
    }
}