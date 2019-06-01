package unimelb.bitbox.util;

import unimelb.bitbox.RemotePeer;

public interface GenericSocket {
    
    public String receive();
    
    public boolean send(String message);
    
    public void disconnect(RemotePeer remotePeer);

    public int getBlockSize();
}
