package unimelb.bitbox.actions;

public interface Action {
    public void execute();

    public int compare(Action action);

    public void send();
}