package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import unimelb.bitbox.Client;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;

public class DirectoryCreateRequest implements Action {

    private Socket socket;
    private static final String command = "DIRECTORY_CREATE_REQUEST";
    private String pathName;
    private Client client;

    public DirectoryCreateRequest(Socket socket, String pathName, Client client) {
        this.client = client;
        this.socket = socket;
        this.pathName = pathName;
    }

    public DirectoryCreateRequest(Socket socket, Document message, Client client) {
        this.client = client;
        this.socket = socket;
        this.pathName = message.getString("pathName");
    }

    @Override
    public void execute(FileSystemManager fileSystemManager) {
        String message = "";
        Boolean status = false;

        if (!fileSystemManager.isSafePathName(pathName)) {
            message = "unsafe pathname given";
        } else if (fileSystemManager.dirNameExists(pathName)) {
            message = "pathname already exists";
        } else if (status = fileSystemManager.makeDirectory(pathName)) {
            message = "directory created";
        } else {
            message = "there was a problem creating the directory";
        }

        Action response = new DirectoryCreateResponse(socket, pathName, message, status, client);
        response.send();
    }

    @Override
    public boolean compare(Document message) {
        boolean correctCommand = message.getString("command").equals("DIRECTORY_CREATE_RESPONSE");
        if (!correctCommand) {
            return false;
        }
        
        boolean matchingPath = message.getString("pathName").equals(this.pathName);
        
        return correctCommand && matchingPath;
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

        message.append("command", command);
        message.append("pathName", pathName);

        return message.toJson();
    }

}
