package unimelb.bitbox;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.logging.Logger;

import unimelb.bitbox.util.CheckResendAndTimeout;
import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.GenerateSyncEventInterval;
import unimelb.bitbox.util.GenericSocket;
import unimelb.bitbox.util.GenericSocketFactory;

public class Peer extends Thread {
    private static Logger log = Logger.getLogger(Peer.class.getName());
    public static GenericSocketFactory socketFactory;
    public static int maximumIncommingConnections;
    private static Object peerSearchLock;
    public static Set<RemotePeer> connectedPeers = new HashSet<RemotePeer>();

    public static void main(String[] args) throws IOException, NumberFormatException, NoSuchAlgorithmException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tc] %2$s %4$s: %5$s%n");
        log.info("BitBox Peer starting...");
        Configuration.getConfiguration();

        maximumIncommingConnections = Integer
                .parseInt(Configuration.getConfigurationValue("maximumIncommingConnections"));

        
        Peer peer = new Peer();
        peer.start();
        ServerMain server = new ServerMain();
        new PeerSearcher();
        ClientServer clientServer = new ClientServer(
                Integer.parseInt(Configuration.getConfigurationValue("clientPort")),
                Configuration.getConfigurationValue("authorized_keys"));
        clientServer.start();

        Timer syncIntervalTimer = new Timer();
        int syncInterval = Integer.parseInt(Configuration.getConfigurationValue("syncInterval")) * 1000;
        syncIntervalTimer.schedule(new GenerateSyncEventInterval(server), syncInterval, syncInterval);
        
        
        Timer timeoutTimer = new Timer();
        timeoutTimer.schedule(new CheckResendAndTimeout(), 1, 1);
    }

    public static Object getPeerSearchLock() {
        return Peer.peerSearchLock;
    }

    public void run() {
        Peer.peerSearchLock = new Object();
        socketFactory = new GenericSocketFactory();
        GenericSocket socket;
        while (true) {
            socket = socketFactory.createIncomingSocket();
            if (socket != null) {
                Peer.connectedPeers.add(new RemotePeer(socket, ServerMain.fileSystemManager));
            }
        }
    }
}
