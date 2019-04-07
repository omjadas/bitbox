package unimelb.bitbox;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import unimelb.bitbox.util.Configuration;

public class Peer extends Thread {
    private static Logger log = Logger.getLogger(Peer.class.getName());
    private ServerSocket serverSocket;

    public static void main(String[] args) throws IOException, NumberFormatException, NoSuchAlgorithmException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tc] %2$s %4$s: %5$s%n");
        log.info("BitBox Peer starting...");
        Configuration.getConfiguration();

        new ServerMain();

        // new Client("127.0.0.1", 3467);
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(Integer.parseInt(Configuration.getConfigurationValue("port")));

            while (true) {
                new Client(serverSocket.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
