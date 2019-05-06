package unimelb.bitbox;

import java.util.LinkedList;
import java.util.Queue;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.HostPort;

public class PeerSearcher extends Thread {
    public static volatile Queue<HostPort> potentialClients = new LinkedList<HostPort>();

    public PeerSearcher() {
        String peerList = Configuration.getConfigurationValue("peers");

        if (peerList.equals("")) {
            return;
        }

        String[] peers = peerList.split(",");

        for (String client : peers) {
            String[] clientDetails = client.split(":");

            HostPort clientHostPort = new HostPort(clientDetails[0], Integer.parseInt(clientDetails[1]));
            potentialClients.add(clientHostPort);
        }

        this.start();
    }

    public synchronized void run() {
        while (!isInterrupted()) {
            synchronized(Peer.getPeerSearchLock()) {
                while (PeerSearcher.potentialClients.size() == 0 || RemotePeer.getNumberIncomingEstablishedConnections() == Peer.maximumIncommingConnections) {
                    try {
                        Peer.getPeerSearchLock().wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }           

            HostPort potentialClient = potentialClients.remove();
            new RemotePeer(potentialClient.host, potentialClient.port, ServerMain.fileSystemManager);
        }
    }
}
