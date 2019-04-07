package unimelb.bitbox.actions;

import java.net.Socket;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager.FileDescriptor;

public class FileBytesResponse implements Action {

    private Socket socket;
    private static final String command = "FILE_BYTES_RESPONSE";
    private FileDescriptor fileDescriptor;
    private String pathName;
    private int position;
    private int length;
    private String content;
    private String message;
    private Boolean status;

    public FileBytesResponse(Socket socket, FileDescriptor fileDescriptor, String pathName, int position, int length,
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

    @Override
    public void execute() {

    }

    @Override
    public int compare(Action action) {
        return 0;
    }

    @Override
    public void send() {

    }

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