package unimelb.bitbox;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import unimelb.bitbox.util.Configuration;

public class Peer extends Thread {
    private static Logger log = Logger.getLogger(Peer.class.getName());
    private ServerSocket serverSocket;
    public static int maximumIncommingConnections;

    public static void main(String[] args) throws IOException, NumberFormatException, NoSuchAlgorithmException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tc] %2$s %4$s: %5$s%n");
        log.info("BitBox Peer starting...");
        Configuration.getConfiguration();

        maximumIncommingConnections = Integer
                .parseInt(Configuration.getConfigurationValue("maximumIncommingConnections"));

        Peer peer = new Peer();
        peer.start();
        new ServerMain();
        new ClientSearcher();
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(Integer.parseInt(Configuration.getConfigurationValue("port")));

            while (true) {
                new Client(serverSocket.accept(), ServerMain.fileSystemManager);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
