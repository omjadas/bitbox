package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.FileDescriptor;

public class FileDeleteRequest implements Action {

    private Socket socket;
    private static final String command = "FILE_DELETE_REQUEST";
    private FileDescriptor fileDescriptor;
    private String pathName;

    public FileDeleteRequest(Socket socket, FileDescriptor fileDescriptor, String pathName) {
        this.socket = socket;
        this.fileDescriptor = fileDescriptor;
        this.pathName = pathName;
    }

    public FileDeleteRequest(Socket socket, Document message) {
        this.socket = socket;
        this.fileDescriptor = new FileDescriptor(message);
        this.pathName = message.getString("pathName");
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

        Action response = new FileDeleteResponse(socket, fileDescriptor, pathName, message, status);
        response.send();
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

        return message.toJson();
    }

}