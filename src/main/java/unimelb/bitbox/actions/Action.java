package unimelb.bitbox.actions;

import java.util.logging.Logger;

import unimelb.bitbox.util.FileSystemManager;

public interface Action {
    public static Logger log = Logger.getLogger(Action.class.getName());

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
     * Send the action to the corresponding client
     */
    public void send();
}