package unimelb.bitbox;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    public static ArrayList<Client> establishedClients;
    private Socket clientSocket;

    public Client(String address, int port) {
        try {
            this.clientSocket = new Socket(address, port);
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
