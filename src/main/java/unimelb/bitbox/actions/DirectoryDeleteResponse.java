package unimelb.bitbox.actions;

import java.net.Socket;
import unimelb.bitbox.util.Document;

public class DirectoryDeleteResponse implements Action {

    private Socket socket;
    private static final String command = "DIRECTORY_DELETE_RESPONSE";
    private String pathName;
    private String message;
    private Boolean status;

    public DirectoryDeleteResponse(Socket socket, String pathName, String message, Boolean status) {
        this.socket = socket;
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

        message.append("command", command);
        message.append("pathName", pathName);
        message.append("message", this.message);
        message.append("status", status);

        return message.toJson();
    }

}