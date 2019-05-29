package unimelb.bitbox.commands;

import unimelb.bitbox.util.Document;

public class AuthResponse implements Command {
    private static final String command = "AUTH_RESPONSE";
    private String AES128;
    private Boolean status;
    private String message;

    public AuthResponse(String AES128, Boolean status, String message) {
        this.AES128 = AES128;
        this.status = status;
        this.message = message;
    }

    @Override
    public void execute() {

    }

    @Override
    public String getPayload() {
        Document payload = new Document();
        payload.append("command", command);
        if (this.AES128 != null) {
            payload.append("AES128", this.AES128);
        }
        payload.append("status", this.status);
        payload.append("message", this.message);
        return payload.toJson();
    }
}