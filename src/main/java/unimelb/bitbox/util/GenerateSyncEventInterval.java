package unimelb.bitbox.util;

import java.util.ArrayList;
import java.util.TimerTask;

import unimelb.bitbox.ServerMain;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;

public class GenerateSyncEventInterval extends TimerTask {
    private ServerMain server;
    
    public GenerateSyncEventInterval(ServerMain server) {
        this.server = server;
    }
    
    public void run() {
        ArrayList<FileSystemEvent> events = ServerMain.getFileSystemManager().generateSyncEvents();
        
        for (FileSystemEvent event : events) {
            this.server.processFileSystemEvent(event);
        }
    }
}
