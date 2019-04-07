package unimelb.bitbox.actions;

import java.net.Socket;
import unimelb.bitbox.util.Document;

public class InvalidProtocol implements Action {

    private Socket socket;
    private static final String command = "INVALID_PROTOCOL";
    private String message;

    public InvalidProtocol(Socket socket, String message) {
        this.socket = socket;
        this.message = message;
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
        message.append("message", message);

        return message.toJson();
    }

}