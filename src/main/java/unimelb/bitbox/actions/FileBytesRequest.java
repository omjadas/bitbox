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

public class FileBytesRequest implements Action {

    private Socket socket;
    private static final String command = "FILE_BYTES_REQUEST";
    private FileDescriptor fileDescriptor;
    private String pathName;
    private long position;
    private long length;

    public FileBytesRequest(Socket socket, FileDescriptor fileDescriptor, String pathName, long position, long length) {
        this.socket = socket;
        this.fileDescriptor = fileDescriptor;
        this.pathName = pathName;
        this.position = position;
        this.length = length;
    }

    public FileBytesRequest(Socket socket, Document message) {
        this.socket = socket;
        this.fileDescriptor = new FileDescriptor(message);
        this.pathName = message.getString("pathName");
        this.position = message.getLong("position");
        this.length = message.getLong("length");
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        String message = "";
        String content = "";
        Boolean status = false;

        try {
            ByteBuffer buf = fileSystemManager.readFile(fileDescriptor.md5, position, length);
            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);
            content = Base64.getEncoder().encodeToString(bytes);
            status = true;
            message = "successful read";
        } catch (NoSuchAlgorithmException | IOException e) {
            message = "unsuccessful read";
        }

        Action response = new FileBytesResponse(socket, fileDescriptor, pathName, position, length, content, message,
                status);
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
            e.printStackTrace();
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

        return message.toJson();
    }

}