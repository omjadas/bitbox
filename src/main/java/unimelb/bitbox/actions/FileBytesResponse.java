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

public class FileBytesResponse extends Thread implements Action {

    private GenericSocket socket;
    private static final String command = "FILE_BYTES_RESPONSE";
    private FileDescriptor fileDescriptor;
    private String pathName;
    private long position;
    private long length;
    private String content;
    private String message;
    private Boolean status;
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

    public FileBytesResponse(GenericSocket socket, FileDescriptor fileDescriptor, String pathName, long position,
            long length, String content, String message, Boolean status, RemotePeer remotePeer) {
        this.socket = socket;
        this.fileDescriptor = fileDescriptor;
        this.pathName = pathName;
        this.position = position;
        this.length = length;
        this.content = content;
        this.message = message;
        this.status = status;
        this.remotePeer = remotePeer;
    }

    public FileBytesResponse(GenericSocket socket, Document message, RemotePeer remotePeer) {
        this.socket = socket;
        this.fileDescriptor = new FileDescriptor(message);
        this.pathName = message.getString("pathName");
        this.position = message.getLong("position");
        this.length = message.getLong("length");
        this.content = message.getString("content");
        this.message = message.getString("message");
        this.status = message.getBoolean("status");
        this.remotePeer = remotePeer;
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        this.fileSystemManager = fileSystemManager;
        this.start();
    }

    @Override
    public boolean compare(Document action) {
        return true;
    }

    @Override
    public void send() {
        this.sendTime = (new Date()).getTime();
        this.attempts += 1;
        socket.send(toJSON());
        log.info("Sent to " + this.remotePeer.getHost() + ":" + this.remotePeer.getPort() + ": " + toJSON());
    }

    @Override
    public void run() {
        try {
            if (fileSystemManager.writeFile(pathName, ByteBuffer.wrap(Base64.getDecoder().decode(content)), position)) {
                if (!fileSystemManager.checkWriteComplete(pathName)) {
                    Action bytes = new FileBytesRequest(socket, fileDescriptor, pathName, position + length,
                            (fileDescriptor.fileSize - (position + length)) < length
                                    ? (fileDescriptor.fileSize - (position + length))
                                    : length,
                            remotePeer);
                    bytes.send();
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            log.info("Error while writing bytes to disk");
        }
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
        message.append("content", content);
        message.append("message", this.message);
        message.append("status", status);

        return message.toJson();
    }

}