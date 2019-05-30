package unimelb.bitbox.commands;

import unimelb.bitbox.util.Document;

public class ConnectPeerResponse implements Command {
    private static final String command = "CONNECT_PEER_RESPONSE";
    private boolean status;
    private String message;

    public ConnectPeerResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String execute() {
        return null;
    }

    @Override
    public String getPayload() {
        Document payload = new Document();
        payload.append("command", command);
        payload.append("status", this.status);
        payload.append("message", this.message);
        return payload.toJson();
    }
}