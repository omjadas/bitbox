package unimelb.bitbox.actions;

public class HandshakeRequest implements Action {

    private static final String command = "HANDSHAKE_REQUEST";

    public HandshakeRequest() {

    }

    @Override
    public void execute() {

    }

    @Override
    public int compare(Action action) {
        return 0;
    }

}