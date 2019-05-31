package unimelb.bitbox.commands;

public interface Command {
    public String execute();

    public String getPayload();
}