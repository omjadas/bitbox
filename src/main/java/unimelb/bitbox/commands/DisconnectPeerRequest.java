package unimelb.bitbox.commands;

public class DisconnectPeerRequest implements Command {
    private static final String command = "DISCONNECT_PEER_REQUEST";
    private String host;
    private long port;

    public DisconnectPeerRequest(String host, long port) {
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