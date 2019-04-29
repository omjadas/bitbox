package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.FileDescriptor;

public class FileBytesResponse implements Action {

    private Socket socket;
    private static final String command = "FILE_BYTES_RESPONSE";
    private FileDescriptor fileDescriptor;
    private String pathName;
    private long position;
    private long length;
    private String content;
    private String message;
    private Boolean status;

    public FileBytesResponse(Socket socket, FileDescriptor fileDescriptor, String pathName, long position, long length,
            String content, String message, Boolean status) {
        this.socket = socket;
        this.fileDescriptor = fileDescriptor;
        this.pathName = pathName;
        this.position = position;
        this.length = length;
        this.content = content;
        this.message = message;
        this.status = status;
    }

    public FileBytesResponse(Socket socket, Document message) {
        this.socket = socket;
        this.fileDescriptor = new FileDescriptor(message);
        this.pathName = message.getString("pathName");
        this.position = message.getLong("position");
        this.length = message.getLong("length");
        this.content = message.getString("content");
        this.message = message.getString("message");
        this.status = message.getBoolean("status");
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        try {
            if (fileSystemManager.writeFile(pathName, ByteBuffer.wrap(Base64.getDecoder().decode(content)), position)) {
                if (!fileSystemManager.checkWriteComplete(pathName)) {
                    Action bytes = new FileBytesRequest(socket, fileDescriptor, pathName, position + length,
                            (fileDescriptor.fileSize - (position + length)) < length
                                    ? (fileDescriptor.fileSize - (position + length))
                                    : length);
                    bytes.send();
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public int compare(Action action) {
        return 0;
    }

    @Override
    public void send() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            out.write(toJSON());
            out.newLine();
            out.flush();
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
        message.append("position", position);
        message.append("length", length);
        message.append("content", content);
        message.append("message", this.message);
        message.append("status", status);

        return message.toJson();
    }

}