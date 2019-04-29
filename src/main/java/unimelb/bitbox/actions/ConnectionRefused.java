package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

import unimelb.bitbox.Client;
import unimelb.bitbox.ClientSearcher;
import unimelb.bitbox.Peer;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.HostPort;

public class ConnectionRefused implements Action {

    private Socket socket;
    private static final String command = "CONNECTION_REFUSED";
    private String message;
    private Document parsedJSON;
        
    public ConnectionRefused(Socket socket, String message) {
        this.socket = socket;
        this.message = message;
    }

    public ConnectionRefused(Socket socket, Document message) {
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
            
            HostPort clientHostPort = new HostPort(host, (int) port);
            ClientSearcher.potentialClients.add(clientHostPort);
            System.out.println(ClientSearcher.potentialClients.toString());
            synchronized(Peer.getClientSearchLock()) {
                Peer.getClientSearchLock().notifyAll();
            }  
        }

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