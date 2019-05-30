package unimelb.bitbox.commands;

public class ConnectPeerRequest implements Command {
    private static final String command = "CONNECT_PEER_REQUEST";
    private String host;
    private long port;

    public ConnectPeerRequest(String host, long port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String execute() {
        return null;
    }

    @Override
    public String getPayload() {
        return null;
    }
}