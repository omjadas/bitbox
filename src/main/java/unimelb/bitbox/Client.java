package unimelb.bitbox;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    public static ArrayList<Client> establishedClients;
    private Socket clientSocket;
    public String host;
    public int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            this.clientSocket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Client(Socket socket) {

    }

    public void initiateHandshake() {

    }

    public void respondToHandshake() {

    }
}
