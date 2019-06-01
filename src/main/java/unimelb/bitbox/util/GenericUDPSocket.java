package unimelb.bitbox.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import unimelb.bitbox.RemotePeer;

public class GenericUDPSocket implements GenericSocket {

    private DatagramSocket udpSocket;
    private DatagramPacket currentPacket;

    private String peerHost;
    private int peerPort;

    private int blockSize;

    /**
     * Initialize outgoing UDP sockets
     * 
     * @param datagramSocket
     * @param blockSize
     * @param host
     * @param port
     */
    public GenericUDPSocket(DatagramSocket datagramSocket, int blockSize, String host, int port) {
        this.udpSocket = datagramSocket;
        this.blockSize = blockSize;
        this.peerHost = host;
        this.peerPort = port;

        try {
            this.udpSocket.connect(new InetSocketAddress(this.peerHost, this.peerPort));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public GenericUDPSocket(DatagramSocket datagramSocket, DatagramPacket packet, int blockSize)
            throws PeerAlreadyConnectedException {
        this.udpSocket = datagramSocket;
        this.blockSize = blockSize;
        this.currentPacket = packet;
        this.peerHost = this.currentPacket.getAddress().getHostAddress();
        this.peerPort = this.currentPacket.getPort();

        for (RemotePeer remotePeer : RemotePeer.establishedPeers) {
            if (remotePeer.getHost().equals(peerHost) && remotePeer.getPort() == peerPort) {
                throw new PeerAlreadyConnectedException("Peer already connected");
            }
        }

        try {
            this.udpSocket.connect(new InetSocketAddress(this.peerHost, this.peerPort));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String receive() {
        if (this.currentPacket != null) {
            String receivedMessage = new String(this.currentPacket.getData(), 0, this.currentPacket.getLength());
            this.currentPacket = null;

            return receivedMessage;
        } else {
            try {
                byte[] receive = new byte[65535];
                DatagramPacket packet = new DatagramPacket(receive, receive.length);
                this.udpSocket.receive(packet);

                return new String(packet.getData(), 0, packet.getLength());
            } catch (IOException e) {
                return null;
            }
        }
    }

    @Override
    public boolean send(String message) {
        try {
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length,
                    InetAddress.getByName(this.peerHost), this.peerPort);

            this.udpSocket.send(packet);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void disconnect(RemotePeer remotePeer) {
        remotePeer.setIsConnected(false);
    }

    @Override
    public int getBlockSize() {
        return this.blockSize;
    }
}
