package unimelb.bitbox.actions;

import java.net.Socket;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager.FileDescriptor;

public class FileDeleteResponse implements Action {

    private Socket socket;
    private static final String command = "FILE_DELETE_RESPONSE";
    private FileDescriptor fileDescriptor;
    private String pathName;
    private String message;
    private Boolean status;

    public FileDeleteResponse(Socket socket, FileDescriptor fileDescriptor, String pathName, String message, Boolean status) {
        this.socket = socket;
        this.fileDescriptor = fileDescriptor;
        this.pathName = pathName;
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
        message.append("message", this.message);
        message.append("status", status);

        return message.toJson();
    }

}