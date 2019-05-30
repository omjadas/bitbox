package unimelb.bitbox.commands;

import unimelb.bitbox.util.Document;

public class AuthRequest implements Command {
    private static final String command = "AUTH_REQUEST";
    private String identity;

    public AuthRequest(String identity) {
        this.identity = identity;
    }

    @Override
    public String execute() {
        return null;
    }

    @Override
    public String getPayload() {
        Document payload = new Document();
        payload.append("command", command);
        payload.append("identity", this.identity);
        return payload.toJson();
    }
}