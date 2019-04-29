package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.FileDescriptor;
import unimelb.bitbox.ServerMain;

public class FileModifyRequest implements Action {

    private Socket socket;
    private static String command = "FILE_MODIFY_REQUEST";
    private FileDescriptor fileDescriptor;
    private String pathName;

    public FileModifyRequest(Socket socket, FileDescriptor fileDescriptor, String pathName) {
        this.socket = socket;
        this.fileDescriptor = fileDescriptor;
        this.pathName = pathName;
    }

    public FileModifyRequest(Socket socket, Document message) {
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
        } else if (fileSystemManager.fileNameExists(pathName, fileDescriptor.md5)) {
            message = "file already exists with matching content";
        } else {
            try {
                if (status = fileSystemManager.modifyFileLoader(pathName, fileDescriptor.md5,
                        fileDescriptor.lastModified)) {
                    message = "file loader ready";
                } else {
                    message = "there was a problem modifying the file";
                }
            } catch (IOException e) {
                message = "there was a problem modifying the file";
            }
        }

        Action response = new FileModifyResponse(socket, fileDescriptor, pathName, message, status);
        response.send();

        if (status) {
            try {
                if (!fileSystemManager.checkShortcut(pathName)) {
                    int blockSize = Integer.parseInt(Configuration.getConfigurationValue("blockSize"));
                    Action bytes = new FileBytesRequest(socket, fileDescriptor, pathName, 0,
                            fileDescriptor.fileSize < blockSize ? fileDescriptor.fileSize : blockSize);
                    bytes.send();
                }
            } catch (NumberFormatException | NoSuchAlgorithmException | IOException e) {
                int blockSize = Integer.parseInt(Configuration.getConfigurationValue("blockSize"));
                Action bytes = new FileBytesRequest(socket, fileDescriptor, pathName, 0,
                        fileDescriptor.fileSize < blockSize ? fileDescriptor.fileSize : blockSize);
                bytes.send();
            }
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

        return message.toJson();
    }

}