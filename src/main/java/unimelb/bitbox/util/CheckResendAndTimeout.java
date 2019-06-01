package unimelb.bitbox.util;

import java.util.Date;
import java.util.TimerTask;

import unimelb.bitbox.Peer;
import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.actions.Action;

public class CheckResendAndTimeout extends TimerTask{
    
    private int timeoutInterval;
    private int numRetries;
    
    public CheckResendAndTimeout() {
        this.timeoutInterval =  Integer
                .parseInt(Configuration.getConfigurationValue("timeoutSeconds"))*1000;
        this.numRetries = Integer
                .parseInt(Configuration.getConfigurationValue("numRetries"));
    }
    

    @Override
    public void run() {        
        
        for (RemotePeer peer : Peer.connectedPeers) {
            if (this.timedOut(peer)) {
                peer.disconnect();
            }
        }
    }
    
    private boolean timedOut(RemotePeer peer) {
        long currentTime = (new Date()).getTime();
        for (Action action : peer.getWaitingActions()) {
            if (currentTime-action.getSendTime() > this.timeoutInterval) {
                if (action.getAttempts() == this.numRetries) {
                    return true;
                } else {
                    action.send();
                }
            }
        }
        
        return false;
    }
    
}
