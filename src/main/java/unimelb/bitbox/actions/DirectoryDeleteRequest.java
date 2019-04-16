package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import unimelb.bitbox.util.Document;

public class DirectoryDeleteRequest implements Action {

    private Socket socket;
    private static final String command = "DIRECTORY_DELETE_REQUEST";
    private String pathName;

    public DirectoryDeleteRequest(Socket socket, String pathName) {
        this.socket = socket;
        this.pathName = pathName;
    }

    public DirectoryDeleteRequest(Socket socket, Document message) {
        this.socket = socket;
        this.pathName = message.getString("pathName");
    }
    
    public DirectoryDeleteRequest(Socket socket, String pathName, FileSystemManager fileSystemManager) {
        this.socket = socket;
        this.pathName = pathName;
        this.fileSystemManager = fileSystemManager;
    }

    @Override
    public void execute() {
        String message = "";
        Boolean status = true;

        // TODO: Execute action
        
        status = fileSystemManager.deleteDirectory(pathName);
        
        if (status) {
            message = "directory deleted";
        } else {
            Boolean isSafePath = fileSystemManager.isSafePathName(pathName);
            Boolean dirNameExist = fileSystemManager.dirNameExists(pathName);
            
            if (!isSafePath) {
                message = "unsafe pathname given";
            } else if (!dirNameExist) {
                message = "pathname does not exist";
            } else {
                message = "there was problem deleting directory";
            }
            
        }

        Action response = new DirectoryDeleteResponse(socket, pathName, message, status);
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
