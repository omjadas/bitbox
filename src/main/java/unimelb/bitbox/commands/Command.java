package unimelb.bitbox.commands;

public interface Command {
    public void execute();

    public String getPayload();
}