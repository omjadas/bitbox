package unimelb.bitbox.commands;

import java.util.ArrayList;

import unimelb.bitbox.util.Document;

public class ListPeersResponse implements Command {
    private static final String command = "LIST_PEERS_RESPONSE";
    private Document peers;

    public ListPeersResponse(Document peers) {
        this.peers = peers;
    }

    @Override
    public String execute() {
        return null;
    }

    @Override
    public String getPayload() {
        Document payload = new Document();
        payload.append("command", command);
        payload.append("peers", (ArrayList<Document>) peers.get("peers"));
        return payload.toJson();
    }
}