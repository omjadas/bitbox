package unimelb.bitbox;

import java.net.Socket;

public class RemoteClient{
    Socket socket;
    public RemoteClient(Socket socket) {
        this.socket = socket;
    }   
}