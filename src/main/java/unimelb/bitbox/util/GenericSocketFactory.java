package unimelb.bitbox.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GenericSocketFactory {
    public enum Protocol {
        TCP, UDP
    }

    private int port;
    public Protocol runtimeProtocol;

    private int blockSize;

    private ServerSocket serverSocket;

    public GenericSocketFactory() {
        String mode = Configuration.getConfigurationValue("mode");
        this.runtimeProtocol = mode.equals("tcp") ? Protocol.TCP : Protocol.UDP;

        String getPortType = this.runtimeProtocol == Protocol.TCP ? "port" : "udpPort";
        this.port = Integer.parseInt(Configuration.getConfigurationValue(getPortType));

        int blockSize = Integer.parseInt(Configuration.getConfigurationValue("blockSize"));
        this.blockSize = this.runtimeProtocol == Protocol.TCP ? blockSize : Math.min(blockSize, 8192);

        if (this.runtimeProtocol == Protocol.TCP) {
            try {
                this.serverSocket = new ServerSocket(this.port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public GenericSocket createIncomingSocket() {
        try {
            if (this.runtimeProtocol == Protocol.TCP) {
                return new GenericTCPSocket(this.serverSocket.accept(), this.blockSize);
            } else {
                return new GenericUDPSocket(this.port, this.blockSize);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public GenericSocket createOutgoingSocket(String host, int port) {
        try {
            if (this.runtimeProtocol == Protocol.TCP) {
                return new GenericTCPSocket(new Socket(host, port), this.blockSize);
            } else {
                return new GenericUDPSocket(this.port, this.blockSize, host, port);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
