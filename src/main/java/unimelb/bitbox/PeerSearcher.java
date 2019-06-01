package unimelb.bitbox;

import java.util.LinkedList;
import java.util.Queue;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.HostPort;

public class PeerSearcher extends Thread {
    public static volatile Queue<HostPort> potentialPeers = new LinkedList<HostPort>();

    public PeerSearcher() {
        String peerList = Configuration.getConfigurationValue("peers");

        if (peerList.equals("")) {
            return;
        }

        String[] peers = peerList.split(",");

        for (String peer : peers) {
            String[] peerDetails = peer.split(":");

            HostPort peerHostPort = new HostPort(peerDetails[0], Integer.parseInt(peerDetails[1]));
            potentialPeers.add(peerHostPort);
        }

        this.start();
    }

    public synchronized void run() {
        while (!isInterrupted()) {
            synchronized(Peer.getPeerSearchLock()) {
                while (PeerSearcher.potentialPeers.size() == 0 || RemotePeer.getNumberIncomingEstablishedConnections() == Peer.maximumIncommingConnections) {
                    try {
                        Peer.getPeerSearchLock().wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }           

            HostPort potentialPeer = potentialPeers.remove();
            Peer.connectedPeers.add(new RemotePeer(potentialPeer.host, potentialPeer.port, ServerMain.fileSystemManager));
        }
    }
}
