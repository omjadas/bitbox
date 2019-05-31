package unimelb.bitbox.util;

public class PeerAlreadyConnectedException extends Exception {
    public PeerAlreadyConnectedException(String errorMessage) {
        super(errorMessage);
    }
}