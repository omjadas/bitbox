package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import unimelb.bitbox.util.Document;

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
    
    public DirectoryCreateRequest(Socket socket, String pathName, FileSystemManager fileSystemManager) {
        this.socket = socket;
        this.pathName = pathName;
        this.fileSystemManager = fileSystemManager;
    }

    @Override
    public void execute() {
        String message = "";
        Boolean status = true;

        // TODO: Execute action
        
        status = fileSystemManager.makeDirectory(pathName);
        
        if (status) {
            message = "directory created";
        } else {
            Boolean isSafePath = fileSystemManager.isSafePathName(pathName);
            Boolean dirNameExists = fileSystemManager.dirNameExists(pathName);
            
            if (!isSafePath) {
                message = "unsafe pathname given";
            } else if (dirNameExists) {
                message = "pathname already exists";
            } else {
                message = "there was problem creating the directory";
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
