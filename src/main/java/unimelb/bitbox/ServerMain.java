package unimelb.bitbox;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.FileSystemObserver;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;

public class ServerMain implements FileSystemObserver {
    private static Logger log = Logger.getLogger(ServerMain.class.getName());
    protected static FileSystemManager fileSystemManager;

    public ServerMain() throws NumberFormatException, IOException, NoSuchAlgorithmException {
        fileSystemManager = new FileSystemManager(Configuration.getConfigurationValue("path"), this);
    }
    
    public static FileSystemManager getFileSystemManager() {
        return ServerMain.fileSystemManager;
    }

    @Override
    public void processFileSystemEvent(FileSystemEvent fileSystemEvent) {
        for (RemotePeer client : RemotePeer.establishedClients) {
            client.processEvent(fileSystemEvent);
        }
    }
}
