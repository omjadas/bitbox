package unimelb.bitbox.actions;

import java.util.logging.Logger;

import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;

public interface Action {
    public long getSendTime();
    
    public int getAttempts();
    
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
    public boolean compare(Document message);

    /**
     * Send the action to the corresponding peer
     */
    public void send();
    
    
}