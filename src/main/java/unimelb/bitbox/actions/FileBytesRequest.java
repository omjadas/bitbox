package unimelb.bitbox.actions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

import unimelb.bitbox.FileDescriptor;
import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.GenericSocket;

public class FileBytesRequest extends Thread implements Action {

    private GenericSocket socket;
    private static final String command = "FILE_BYTES_REQUEST";
    private FileDescriptor fileDescriptor;
    private String pathName;
    private long position;
    private long length;
    private RemotePeer remotePeer;
    private FileSystemManager fileSystemManager;
    private long sendTime;
    private int attempts = 0;
    
    public long getSendTime() {
        return sendTime;
    }
    
    public int getAttempts() {
        return attempts;
    }


    public FileBytesRequest(GenericSocket socket, FileDescriptor fileDescriptor, String pathName, long position,
            long length, RemotePeer remotePeer) {
        this.socket = socket;
        this.fileDescriptor = fileDescriptor;
        this.pathName = pathName;
        this.position = position;
        this.length = length;
        this.remotePeer = remotePeer;
    }

    public FileBytesRequest(GenericSocket socket, Document message, RemotePeer remotePeer) {
        this.socket = socket;
        this.fileDescriptor = new FileDescriptor(message);
        this.pathName = message.getString("pathName");
        this.position = message.getLong("position");
        this.length = message.getLong("length");
        this.remotePeer = remotePeer;
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        this.fileSystemManager = fileSystemManager;
        this.start();
    }

    @Override
    public boolean compare(Document message) {
        boolean correctCommand = message.getString("command").equals("FILE_BYTES_RESPONSE");
        if (!correctCommand) {
            return false;
        }

        boolean matchingPath = message.getString("pathName").equals(this.pathName);
        boolean matchingFileDesc = this.fileDescriptor.compare(new FileDescriptor(message));
        boolean matchingLength = message.getLong("length") == this.length;
        boolean matchingPos = message.getLong("position") == this.position;

        return (correctCommand && matchingPath && matchingFileDesc && matchingLength && matchingPos);
    }

    @Override
    public void send() {
        this.sendTime = (new Date()).getTime();
        this.attempts += 1;
        socket.send(toJSON());
        log.info("Sent to " + this.remotePeer.getHost() + ":" + this.remotePeer.getPort() + ": " + toJSON());
        this.remotePeer.addToWaitingActions(this);
    }

    @Override
    public void run() {
        String message = "";
        String content = "";
        Boolean status = false;

        try {
            ByteBuffer buf = fileSystemManager.readFile(fileDescriptor.md5, position, length);
            byte[] bytes = new byte[buf.rewind().remaining()];
            buf.get(bytes);
            content = Base64.getEncoder().encodeToString(bytes);
            status = true;
            message = "successful read";
        } catch (NoSuchAlgorithmException | IOException e) {
            message = "unsuccessful read";
        }

        Action response = new FileBytesResponse(socket, fileDescriptor, pathName, position, length, content, message,
                status, remotePeer);
        response.send();
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
        message.append("position", position);
        message.append("length", length);

        return message.toJson();
    }

}