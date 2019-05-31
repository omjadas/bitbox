package unimelb.bitbox.commands;

import java.util.ArrayList;

import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.HostPort;

public class ListPeersRequest implements Command {
    private static final String command = "LIST_PEERS_REQUEST";

    public ListPeersRequest() {
    }

    @Override
    public String execute() {
        Document peers = new Document();
        ArrayList<Document> peerList = new ArrayList<>();
        for (RemotePeer remotePeer : RemotePeer.establishedPeers) {
            peerList.add((new HostPort(remotePeer.getHost(), (int) remotePeer.getPort())).toDoc());
        }
        peers.append("peers", peerList);

        return new ListPeersResponse(peers).getPayload();
    }

    @Override
    public String getPayload() {
        Document payload = new Document();
        payload.append("command", command);
        return payload.toJson();
    }
}