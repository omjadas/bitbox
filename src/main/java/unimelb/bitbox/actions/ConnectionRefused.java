package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

    public ConnectionRefused(Socket socket, Document message) {
        this.socket = socket;
        this.message = message.getString("message");
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
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            out.write(toJSON());
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
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

        for (Client client : Client.establishedClients) {
            Document peer = new Document();
            peer.append("host", client.getHost());
            peer.append("port", client.getPort());

            peers.add(peer);
        }

        message.append("command", command);
        message.append("message", this.message);
        message.append("peers", peers);

        return message.toJson();
    }

}