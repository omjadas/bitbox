package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import unimelb.bitbox.util.Document;

public class DirectoryCreateResponse implements Action {

    private Socket socket;
    private static final String command = "DIRECTORY_CREATE_RESPONSE";
    private String pathName;
    private String message;
    private Boolean status;

    public DirectoryCreateResponse(Socket socket, String pathName, String message, Boolean status) {
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
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            out.write(toJSON());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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