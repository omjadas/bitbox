package unimelb.bitbox.actions;

import java.net.Socket;
import java.util.ArrayList;
import unimelb.bitbox.Client;
import unimelb.bitbox.util.Document;

public class ConnectionRefused implements Action {

    private Socket socket;
    private static final String command = "CONNECTION_REFUSED";
    private String message;

    public ConnectionRefused(Socket socket, String message) {
        this.socket = socket;
        this.message = message;
    }

    @Override
    public void execute() {

    }

    @Override
    public int compare(Action action) {
        return 0;
    }

    @Override
    public void send() {

    }

    private String toJSON() {
        Document message = new Document();
        ArrayList<Document> peers = new ArrayList<Document>();

        for (Client client : Client.establishedClients) {
            Document peer = new Document();
            peer.append("host", client.host);
            peer.append("port", client.port);

            peers.add(peer);
        }

        message.append("command", command);
        message.append("message", this.message);
        message.append("peers", peers);

        return message.toJson();
    }

}