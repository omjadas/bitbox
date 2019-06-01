package unimelb.bitbox.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;

import unimelb.bitbox.RemotePeer;

public class GenericUDPSocket implements GenericSocket {

    private static DatagramSocket udpSocket = null;
    private static HashMap<String, LinkedList<String>> queues = new HashMap<String, LinkedList<String>>();

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
    public GenericUDPSocket(int serverPort, int blockSize, String host, int port) {
        if (GenericUDPSocket.udpSocket == null) {
            try {
                GenericUDPSocket.udpSocket = new DatagramSocket(serverPort);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        this.blockSize = blockSize;
        this.peerHost = host;
        this.peerPort = port;
        try {
            String hostPort = String.format("%s:%d", InetAddress.getByName(host).getHostAddress(), port);
            queues.put(hostPort, new LinkedList<String>());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public GenericUDPSocket(int serverPort, int blockSize) throws PeerAlreadyConnectedException {
        if (GenericUDPSocket.udpSocket == null) {
            try {
                GenericUDPSocket.udpSocket = new DatagramSocket(serverPort);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        this.blockSize = blockSize;

        DatagramPacket packet = getPacket();
        String hostPort = String.format("%s:%d", packet.getAddress().getHostAddress(), packet.getPort());
        this.peerHost = packet.getAddress().getHostAddress();
        this.peerPort = packet.getPort();
        if (queues.containsKey(hostPort)) {
            queues.get(hostPort).add(new String(packet.getData(), 0, packet.getLength()));
            throw new PeerAlreadyConnectedException("peer already connected");
        } else {
            queues.put(hostPort, new LinkedList<String>());
            queues.get(hostPort).add(new String(packet.getData(), 0, packet.getLength()));
        }
    }

    private DatagramPacket getPacket() {
        DatagramPacket packet = null;
        try {
            byte[] receive = new byte[65535];
            packet = new DatagramPacket(receive, receive.length);
            GenericUDPSocket.udpSocket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet;
    }

    private void addToMap(DatagramPacket packet) {
        String hostPort = String.format("%s:%d", packet.getAddress().getHostAddress(), packet.getPort());
        if (queues.containsKey(hostPort)) {
            queues.get(hostPort).add(new String(packet.getData(), 0, packet.getLength()));
        } else {
            queues.put(hostPort, new LinkedList<String>());
            queues.get(hostPort).add(new String(packet.getData(), 0, packet.getLength()));
        }
    }

    @Override
    public String receive() {
        try {
            String hostPort = String.format("%s:%d", InetAddress.getByName(peerHost).getHostAddress(), peerPort);
            while (queues.get(hostPort).size() == 0) {
            }
            return queues.get(hostPort).remove();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean send(String message) {
        try {
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length,
                    InetAddress.getByName(this.peerHost), this.peerPort);

            GenericUDPSocket.udpSocket.send(packet);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void disconnect(RemotePeer remotePeer) {
        try {
            String hostPort = String.format("%s:%d", InetAddress.getByName(peerHost).getHostAddress(), peerPort);
            GenericUDPSocket.queues.remove(hostPort);
            remotePeer.setIsConnected(false);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getBlockSize() {
        return this.blockSize;
    }
}
