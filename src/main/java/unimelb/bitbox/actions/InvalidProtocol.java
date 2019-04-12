package unimelb.bitbox.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

    public InvalidProtocol(Socket socket, Document message) {
        this.socket = socket;
        this.message = message.getString("message");
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

    /**
     * Convert the action to JSON
     * 
     * @return JSON string
     */
    private String toJSON() {
        Document message = new Document();

        message.append("command", command);
        message.append("message", this.message);

        return message.toJson();
    }

}