package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.RemotePeer;
import unimelb.bitbox.FileDescriptor;

public class FileDeleteResponse implements Action {

    private Socket socket;
    private static final String command = "FILE_DELETE_RESPONSE";
    private FileDescriptor fileDescriptor;
    private String pathName;
    private String message;
    private Boolean status;
    private RemotePeer remotePeer;
    
    public FileDeleteResponse(Socket socket, FileDescriptor fileDescriptor, String pathName, String message,
            Boolean status, RemotePeer remotePeer) {
        this.socket = socket;
        this.fileDescriptor = fileDescriptor;
        this.pathName = pathName;
        this.message = message;
        this.status = status;
        this.remotePeer = remotePeer;
    }

    public FileDeleteResponse(Socket socket, Document message, RemotePeer remotePeer) {
        this.socket = socket;
        this.fileDescriptor = new FileDescriptor(message);
        this.pathName = message.getString("pathName");
        this.message = message.getString("message");
        this.status = message.getBoolean("status");
        this.remotePeer = remotePeer;
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {

    }

    @Override
    public boolean compare(Document action) {
        return true;
    }

    @Override
    public void send() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            out.write(toJSON());
            out.newLine();
            out.flush();
            log.info("Sent to " + this.remotePeer.getHost() + ":" + this.remotePeer.getPort() + ": " + toJSON());
        } catch (IOException e) {
            log.info("Socket was closed while sending message");
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
        message.append("message", this.message);
        message.append("status", status);

        return message.toJson();
    }

}