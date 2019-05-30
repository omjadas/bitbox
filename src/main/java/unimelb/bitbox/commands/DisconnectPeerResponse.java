package unimelb.bitbox.commands;

import unimelb.bitbox.util.Document;

public class DisconnectPeerResponse implements Command {
    private static final String command = "DISCONNECT_PEER_RESPONSE";
    private boolean status;
    private String message;

    public DisconnectPeerResponse(boolean status, String message) {
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