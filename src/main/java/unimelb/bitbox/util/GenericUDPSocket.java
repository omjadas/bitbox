package unimelb.bitbox.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class GenericUDPSocket implements GenericSocket {

    private DatagramSocket udpSocket;
    private DatagramPacket currentPacket;

    private String clientHost;
    private int clientPort;

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
        this.clientHost = host;
        this.clientPort = port;

        try {
            this.udpSocket.connect(new InetSocketAddress(this.clientHost, this.clientPort));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public GenericUDPSocket(DatagramSocket datagramSocket, DatagramPacket packet, int blockSize) {
        this.udpSocket = datagramSocket;
        this.blockSize = blockSize;
        this.currentPacket = packet;
        this.clientHost = this.currentPacket.getAddress().getHostAddress();
        this.clientPort = this.currentPacket.getPort();

        try {
            this.udpSocket.connect(new InetSocketAddress(this.clientHost, this.clientPort));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String receive() {
        if (this.currentPacket != null) {
            String receivedMessage = new String(this.currentPacket.getData());
            this.currentPacket = null;

            return receivedMessage;
        } else {
            try {
                byte[] receive = new byte[65535];
                DatagramPacket packet = new DatagramPacket(receive, receive.length);
                this.udpSocket.receive(packet);

                String receivedMessage = new String(packet.getData());
                return receivedMessage;
            } catch (IOException e) {
                return null;
            }
        }
    }

    @Override
    public boolean send(String message) {
        try {
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length,
                    InetAddress.getByName(this.clientHost), this.clientPort);
            
            this.udpSocket.send(packet);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub

    }

}
