package unimelb.bitbox.actions;

import java.util.Date;

import unimelb.bitbox.FileDescriptor;
import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.GenericSocket;

public class FileDeleteRequest implements Action {

    private GenericSocket socket;
    private static final String command = "FILE_DELETE_REQUEST";
    private FileDescriptor fileDescriptor;
    private String pathName;
    private RemotePeer remotePeer;
    private long sendTime;
    private int attempts = 0;
    
    public long getSendTime() {
        return sendTime;
    }
    
    public int getAttempts() {
        return attempts;
    }

    public FileDeleteRequest(GenericSocket socket, FileDescriptor fileDescriptor, String pathName,
            RemotePeer remotePeer) {
        this.socket = socket;
        this.fileDescriptor = fileDescriptor;
        this.pathName = pathName;
        this.remotePeer = remotePeer;
    }

    public FileDeleteRequest(GenericSocket socket, Document message, RemotePeer remotePeer) {
        this.socket = socket;
        this.fileDescriptor = new FileDescriptor(message);
        this.pathName = message.getString("pathName");
        this.remotePeer = remotePeer;
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        String message = "";
        Boolean status = false;

        if (!fileSystemManager.isSafePathName(pathName)) {
            message = "unsafe pathname given";
        } else if (!fileSystemManager.fileNameExists(pathName)) {
            message = "pathname does not exist";
        } else if (status = fileSystemManager.deleteFile(pathName, fileDescriptor.lastModified, fileDescriptor.md5)) {
            message = "file deleted";
        } else {
            message = "there was a problem deleting the file";
        }

        Action response = new FileDeleteResponse(socket, fileDescriptor, pathName, message, status, remotePeer);
        response.send();
    }

    @Override
    public boolean compare(Document message) {
        boolean correctCommand = message.getString("command").equals("FILE_DELETE_RESPONSE");
        if (!correctCommand) {
            return false;
        }

        boolean matchingPath = message.getString("pathName").equals(this.pathName);
        boolean matchingFileDesc = this.fileDescriptor.compare(new FileDescriptor(message));

        return (correctCommand && matchingPath && matchingFileDesc);
    }

    @Override
    public void send() {
        this.sendTime = (new Date()).getTime();
        this.attempts += 1;
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
        Document fileDescriptor = new Document();

        fileDescriptor.append("md5", this.fileDescriptor.md5);
        fileDescriptor.append("lastModified", this.fileDescriptor.lastModified);
        fileDescriptor.append("fileSize", this.fileDescriptor.fileSize);

        message.append("command", command);
        message.append("fileDescriptor", fileDescriptor);
        message.append("pathName", pathName);

        return message.toJson();
    }

}