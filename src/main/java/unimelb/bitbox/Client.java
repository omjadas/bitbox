package unimelb.bitbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import unimelb.bitbox.actions.Action;
import unimelb.bitbox.actions.ConnectionRefused;
import unimelb.bitbox.actions.DirectoryCreateRequest;
import unimelb.bitbox.actions.DirectoryCreateResponse;
import unimelb.bitbox.actions.DirectoryDeleteRequest;
import unimelb.bitbox.actions.DirectoryDeleteResponse;
import unimelb.bitbox.actions.FileBytesRequest;
import unimelb.bitbox.actions.FileBytesResponse;
import unimelb.bitbox.actions.FileCreateRequest;
import unimelb.bitbox.actions.FileCreateResponse;
import unimelb.bitbox.actions.FileDeleteRequest;
import unimelb.bitbox.actions.FileDeleteResponse;
import unimelb.bitbox.actions.FileModifyRequest;
import unimelb.bitbox.actions.FileModifyResponse;
import unimelb.bitbox.actions.HandshakeRequest;
import unimelb.bitbox.actions.HandshakeResponse;
import unimelb.bitbox.actions.InvalidProtocol;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;

public class Client extends Thread {
    public static ArrayList<Client> establishedClients = new ArrayList<Client>();
    private Socket socket;
    private String host;
    private int port;
    private FileSystemManager fileSystemManager;

    private boolean establishedConnection = false;

    public Client(String host, int port, FileSystemManager fileSystemManager) {
        this.host = host;
        this.port = port;
        this.fileSystemManager = fileSystemManager;
        try {
            this.socket = new Socket(host, port);
            HandshakeRequest requestAction = new HandshakeRequest(this.socket, host, port);
            requestAction.send();
            this.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Client(Socket socket, FileSystemManager fileSystemManager) {
        this.socket = socket;
        this.fileSystemManager = fileSystemManager;
        if (establishedClients.size() == Peer.maximumIncommingConnections) {
            new ConnectionRefused(socket, "connection limit reached").send();
            return;
        }
        establishedClients.add(this);

        this.start();
    }

    public void establishConnection() {

    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    private boolean validateRequest(Document message) {
        return true;
    }

    private Action getAction(Document message) {
        Action action = null;
        String command = message.getString("command");

        System.out.println(command);

        if (command.equals("INVALID_PROTOCOL")) {
            action = new InvalidProtocol(socket, message);
        } else if (command.equals("CONNECTION_REFUSED")) {
            action = new ConnectionRefused(socket, message);
        } else if (command.equals("HANDSHAKE_REQUEST")) {
            action = new HandshakeRequest(socket, message);
        } else if (command.equals("HANDSHAKE_RESPONSE")) {
            action = new HandshakeResponse(socket, message);
        } else if (command.equals("FILE_CREATE_REQUEST")) {
            action = new FileCreateRequest(socket, message);
        } else if (command.equals("FILE_CREATE_RESPONSE")) {
            action = new FileCreateResponse(socket, message);
        } else if (command.equals("FILE_DELETE_REQUEST")) {
            action = new FileDeleteRequest(socket, message);
        } else if (command.equals("FILE_DELETE_RESPONSE")) {
            action = new FileDeleteResponse(socket, message);
        } else if (command.equals("FILE_MODIFY_REQUEST")) {
            action = new FileModifyRequest(socket, message);
        } else if (command.equals("FILE_MODIFY_RESPONSE")) {
            action = new FileModifyResponse(socket, message);
        } else if (command.equals("DIRECTORY_CREATE_REQUEST")) {
            action = new DirectoryCreateRequest(socket, message);
        } else if (command.equals("DIRECTORY_CREATE_RESPONSE")) {
            action = new DirectoryCreateResponse(socket, message);
        } else if (command.equals("DIRECTORY_DELETE_REQUEST")) {
            action = new DirectoryDeleteRequest(socket, message);
        } else if (command.equals("DIRECTORY_DELETE_RESPONSE")) {
            action = new DirectoryDeleteResponse(socket, message);
        } else if (command.equals("FILE_BYTES_REQUEST")) {
            action = new FileBytesRequest(socket, message);
        } else if (command.equals("FILE_BYTES_RESPONSE")) {
            action = new FileBytesResponse(socket, message);
        }

        return action;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);

                Document message = Document.parse(inputLine);

                if (validateRequest(message)) {
                    Action action = getAction(message);
                    action.execute(fileSystemManager);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
