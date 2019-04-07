package unimelb.bitbox.actions;

public class ConnectionRefused implements Action {

    private static final String command = "CONNECTION_REFUSED";

    public ConnectionRefused() {

    }

    @Override
    public void execute() {

    }

    @Override
    public int compare(Action action) {
        return 0;
    }

}