package unimelb.bitbox.commands;

public class ConnectPeerRequest implements Command {
    private static final String command = "ConnectPeerRequest";
    private String host;
    private long port;

    public ConnectPeerRequest(String host, long port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void execute() {

    }

    @Override
    public String getPayload() {
        return null;
    }

}