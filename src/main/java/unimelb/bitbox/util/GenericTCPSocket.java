package unimelb.bitbox.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class GenericTCPSocket implements GenericSocket {

    private Socket tcpSocket;
    private int blockSize;
    private BufferedReader in;
    private BufferedWriter out;

    public GenericTCPSocket(Socket socket, int blockSize) {
        this.tcpSocket = socket;
        this.blockSize = blockSize;

        try {
            this.in = new BufferedReader(new InputStreamReader(this.tcpSocket.getInputStream(), "UTF-8"));
            this.out = new BufferedWriter(new OutputStreamWriter(this.tcpSocket.getOutputStream(), "UTF8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public String receive() {
        try {
            return this.in.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean send(String message) {
        try {
            this.out.write(message);
            this.out.newLine();
            this.out.flush();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub
    }
}
