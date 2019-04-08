package unimelb.bitbox.actions;

public interface Action {

    /**
     * Execute the action
     */
    public void execute();

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