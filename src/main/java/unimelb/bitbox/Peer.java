package unimelb.bitbox;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.logging.Logger;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.GenerateSyncEventInterval;

public class Peer extends Thread {
    private static Logger log = Logger.getLogger(Peer.class.getName());
    private ServerSocket serverSocket;
    public static int maximumIncommingConnections;
    private static Object clientSearchLock;

    public static void main(String[] args) throws IOException, NumberFormatException, NoSuchAlgorithmException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tc] %2$s %4$s: %5$s%n");
        log.info("BitBox Peer starting...");
        Configuration.getConfiguration();

        maximumIncommingConnections = Integer
                .parseInt(Configuration.getConfigurationValue("maximumIncommingConnections"));

        Peer peer = new Peer();
        peer.start();
        ServerMain server = new ServerMain();
        new ClientSearcher();
        
        Timer syncIntervalTimer = new Timer();
        int syncInterval = Integer.parseInt(Configuration.getConfigurationValue("syncInterval"))*1000;
        syncIntervalTimer.schedule(new GenerateSyncEventInterval(server), syncInterval, syncInterval);
    }

    public static Object getClientSearchLock() {
        return Peer.clientSearchLock;
    }
    
    public void run() {
        try {
            Peer.clientSearchLock = new Object();
            serverSocket = new ServerSocket(Integer.parseInt(Configuration.getConfigurationValue("port")));

            while (true) {
                new RemotePeer(serverSocket.accept(), ServerMain.fileSystemManager);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
