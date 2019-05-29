package unimelb.bitbox;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

public class ClientServer extends Thread {
    private ServerSocket serverSocket;
    public static HashMap<String, String> authorized_keys = new HashMap<>();

    public ClientServer(int port, String authorized_keys) {
        String[] keys = authorized_keys.split(",", 0);

        // Add keys from config to authorized_keys
        for (String key : keys) {
            ClientServer.authorized_keys.put(key.split(" ", 0)[2], key.split(" ", 0)[1]);
        }

        // Create server socket
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                new RemoteClient(serverSocket.accept());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}