package unimelb.bitbox.actions;

public class InvalidProtocol implements Action {

    private static final String command = "INVALID_PROTOCOL";

    public InvalidProtocol() {

    }

    @Override
    public void execute() {

    }

    @Override
    public int compare(Action action) {
        return 0;
    }

}