package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.Client;
import unimelb.bitbox.FileDescriptor;

public class FileCreateRequest implements Action {

    private Socket socket;
    private static final String command = "FILE_CREATE_REQUEST";
    private FileDescriptor fileDescriptor;
    private String pathName;
    private Client client;

    public FileCreateRequest(Socket socket, FileDescriptor fileDescriptor, String pathName, Client client) {
        this.socket = socket;
        this.fileDescriptor = fileDescriptor;
        this.pathName = pathName;
        this.client = client;
    }

    public FileCreateRequest(Socket socket, Document message, Client client) {
        this.socket = socket;
        this.fileDescriptor = new FileDescriptor(message);
        this.pathName = message.getString("pathName");
        this.client = client;
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        String message = "";
        Boolean status = false;

        if (!fileSystemManager.isSafePathName(pathName)) {
            message = "unsafe pathname given";
        } else if (fileSystemManager.fileNameExists(pathName)) {
            message = "pathname already exists";
        } else
            try {
                if (status = fileSystemManager.createFileLoader(pathName, fileDescriptor.md5, fileDescriptor.fileSize,
                        fileDescriptor.lastModified)) {
                    message = "file loader ready";
                } else {
                    message = "there was a problem creating the file";
                }
            } catch (NoSuchAlgorithmException | IOException e) {
                message = "there was a problem creating the file";
            }

        Action response = new FileCreateResponse(socket, fileDescriptor, pathName, message, status, client);
        response.send();

        if (status) {
            try {
                if (!fileSystemManager.checkShortcut(pathName)) {
                    int blockSize = Integer.parseInt(Configuration.getConfigurationValue("blockSize"));
                    Action bytes = new FileBytesRequest(socket, fileDescriptor, pathName, 0,
                            fileDescriptor.fileSize < blockSize ? fileDescriptor.fileSize : blockSize, client);
                    bytes.send();
                }
            } catch (NumberFormatException | NoSuchAlgorithmException | IOException e) {
                int blockSize = Integer.parseInt(Configuration.getConfigurationValue("blockSize"));
                Action bytes = new FileBytesRequest(socket, fileDescriptor, pathName, 0,
                        fileDescriptor.fileSize < blockSize ? fileDescriptor.fileSize : blockSize, client);
                bytes.send();
            }
        }
    }

    @Override
    public boolean compare(Document message) {        
        boolean correctCommand = message.getString("command").equals("FILE_CREATE_RESPONSE");
        if (!correctCommand) {
            return false;
        }
        
        boolean matchingPath = message.getString("pathName").equals(this.pathName);
        boolean matchingFileDesc = this.fileDescriptor.compare(new FileDescriptor(message));
        
        return (correctCommand && matchingPath && matchingFileDesc);
    }

    @Override
    public void send() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            out.write(toJSON());
            out.newLine();
            out.flush();
            log.info("Sent to " + this.client.getHost() + ":" + this.client.getPort() + ": " + toJSON());
            this.client.addToWaitingActions(this);
        } catch (IOException e) {
            log.info("Socket was closed while sending message");
        }
    }

    /**
     * Convert the action to JSON
     * 
     * @return JSON string
     */
    @Override
    public String toJSON() {
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