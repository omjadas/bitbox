package unimelb.bitbox.util;

public interface GenericSocket {
    
    public String receive();
    
    public boolean send(String message);
    
    public void disconnect();

    public int getBlockSize();
}
