package unimelb.bitbox.actions;

import unimelb.bitbox.util.FileSystemManager;

public interface Action {

    /**
     * Execute the action
     */
    public void execute(FileSystemManager fileSystemManager);

    /**
     * Compare two actions
     * 
     * @param action The action to compare against
     * @return
     */
    public int compare(Action action);

    /**
     * Send the action to the client
     */
    public void send();
}