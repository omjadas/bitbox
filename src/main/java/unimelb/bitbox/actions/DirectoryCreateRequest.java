package unimelb.bitbox.actions;

import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.GenericSocket;

public class DirectoryCreateRequest implements Action {

    private GenericSocket socket;
    private static final String command = "DIRECTORY_CREATE_REQUEST";
    private String pathName;
    private RemotePeer remotePeer;

    public DirectoryCreateRequest(GenericSocket socket, String pathName, RemotePeer remotePeer) {
        this.remotePeer = remotePeer;
        this.socket = socket;
        this.pathName = pathName;
    }

    public DirectoryCreateRequest(GenericSocket socket, Document message, RemotePeer remotePeer) {
        this.remotePeer = remotePeer;
        this.socket = socket;
        this.pathName = message.getString("pathName");
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        String message = "";
        Boolean status = false;

        if (!fileSystemManager.isSafePathName(pathName)) {
            message = "unsafe pathname given";
        } else if (fileSystemManager.dirNameExists(pathName)) {
            message = "pathname already exists";
        } else if (status = fileSystemManager.makeDirectory(pathName)) {
            message = "directory created";
        } else {
            message = "there was a problem creating the directory";
        }

        Action response = new DirectoryCreateResponse(socket, pathName, message, status, remotePeer);
        response.send();
    }

    @Override
    public boolean compare(Document message) {
        boolean correctCommand = message.getString("command").equals("DIRECTORY_CREATE_RESPONSE");
        if (!correctCommand) {
            return false;
        }

        boolean matchingPath = message.getString("pathName").equals(this.pathName);

        return correctCommand && matchingPath;
    }

    @Override
    public void send() {
        socket.send(toJSON());
        log.info("Sent to " + this.remotePeer.getHost() + ":" + this.remotePeer.getPort() + ": " + toJSON());
        this.remotePeer.addToWaitingActions(this);
    }

    /**
     * Convert the action to JSON
     * 
     * @return JSON string
     */
    private String toJSON() {
        Document message = new Document();

        message.append("command", command);
        message.append("pathName", pathName);

        return message.toJson();
    }

}
