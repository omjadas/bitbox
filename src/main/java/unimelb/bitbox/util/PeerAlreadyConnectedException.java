package unimelb.bitbox.util;

public class PeerAlreadyConnectedException extends Exception {
    private static final long serialVersionUID = -3079276230753816298L;

    public PeerAlreadyConnectedException(String errorMessage) {
        super(errorMessage);
    }
}