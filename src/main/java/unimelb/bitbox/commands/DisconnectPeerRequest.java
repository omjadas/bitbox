package unimelb.bitbox.commands;

import unimelb.bitbox.util.Document;

public class DisconnectPeerRequest implements Command {
    private static final String command = "DISCONNECT_PEER_REQUEST";
    private String host;
    private long port;

    public DisconnectPeerRequest(String host, long port) {
        this.host = host;
        this.port = port;
    }

    public DisconnectPeerRequest(Document doc) {
        this.host = doc.getString("host");
        this.port = doc.getLong("port");
    }

    @Override
    public String execute() {
        return null;
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