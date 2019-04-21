package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;

public class DirectoryCreateRequest implements Action {

    private Socket socket;
    private static final String command = "DIRECTORY_CREATE_REQUEST";
    private String pathName;

    public DirectoryCreateRequest(Socket socket, String pathName) {
        this.socket = socket;
        this.pathName = pathName;
    }

    public DirectoryCreateRequest(Socket socket, Document message) {
        this.socket = socket;
        this.pathName = message.getString("pathName");
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        String message = "";
        Boolean status = true;

        Boolean isSafePath = fileSystemManager.isSafePathName(pathName);

        if (!isSafePath) {
            message = "unsafe pathname given";
        } else {
            Boolean dirNameExists = fileSystemManager.dirNameExists(pathName);
            if (dirNameExists) {
                message = "pathname already exists";
            } else {
                status = fileSystemManager.makeDirectory(pathName);
                if (status) {
                    message = "directory created";
                } else {
                    message = "there was a problem creating the directory";
                }
            }
        }

        Action response = new DirectoryCreateResponse(socket, pathName, message, status);
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

        message.append("command", command);
        message.append("pathName", pathName);

        return message.toJson();
    }

}
