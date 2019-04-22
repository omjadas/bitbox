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
import unimelb.bitbox.FileDescriptor;

public class Client extends Thread {
    public static ArrayList<Client> establishedClients = new ArrayList<Client>();
    private Socket clientSocket;
    private String host;
    private int port;

    private boolean establishedConnection = false;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            this.clientSocket = new Socket(host, port);
            HandshakeRequest requestAction = new HandshakeRequest(this.clientSocket, host, port);
            requestAction.send();
            this.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Client(Socket socket) {
        this.clientSocket = socket;
        establishConnection();
        this.start();
    }

    /**
     * Establish a connection with the client
     */
    public void establishConnection() {
        if (establishedClients.size() == Peer.maximumIncommingConnections) {
            new ConnectionRefused(clientSocket, "connection limit reached").send();
            return;
        }
        establishedClients.add(this);
    }

    /**
     * Return the host of the client
     * 
     * @return The host of the client
     */
    public String getHost() {
        return host;
    }

    /**
     * Return the port of the client
     * 
     * @return The port of the client
     */
    public int getPort() {
        return port;
    }

    /**
     * Determine if the message is valid
     * 
     * @param message The received message
     * @return Boolean indication whether the message is valid
     */
    private boolean validateRequest(Document message) {
        return true;
    }

    /**
     * Return an appropriate action for the received message
     * 
     * @param message The received message
     * @return An action corresponding to the revceived message
     */
    private Action getAction(Document message) {
        Action action = null;
        String command = message.getString("command");

        System.out.println(command);

        if (command.equals("INVALID_PROTOCOL")) {
            action = new InvalidProtocol(clientSocket, message);
        } else if (command.equals("CONNECTION_REFUSED")) {
            action = new ConnectionRefused(clientSocket, message);
        } else if (command.equals("HANDSHAKE_REQUEST")) {
            action = new HandshakeRequest(clientSocket, message);
        } else if (command.equals("HANDSHAKE_RESPONSE")) {
            action = new HandshakeResponse(clientSocket, message);
        } else if (command.equals("FILE_CREATE_REQUEST")) {
            action = new FileCreateRequest(clientSocket, message);
        } else if (command.equals("FILE_CREATE_RESPONSE")) {
            action = new FileCreateResponse(clientSocket, message);
        } else if (command.equals("FILE_DELETE_REQUEST")) {
            action = new FileDeleteRequest(clientSocket, message);
        } else if (command.equals("FILE_DELETE_RESPONSE")) {
            action = new FileDeleteResponse(clientSocket, message);
        } else if (command.equals("FILE_MODIFY_REQUEST")) {
            action = new FileModifyRequest(clientSocket, message);
        } else if (command.equals("FILE_MODIFY_RESPONSE")) {
            action = new FileModifyResponse(clientSocket, message);
        } else if (command.equals("DIRECTORY_CREATE_REQUEST")) {
            action = new DirectoryCreateRequest(clientSocket, message);
        } else if (command.equals("DIRECTORY_CREATE_RESPONSE")) {
            action = new DirectoryCreateResponse(clientSocket, message);
        } else if (command.equals("DIRECTORY_DELETE_REQUEST")) {
            action = new DirectoryDeleteRequest(clientSocket, message);
        } else if (command.equals("DIRECTORY_DELETE_RESPONSE")) {
            action = new DirectoryDeleteResponse(clientSocket, message);
        } else if (command.equals("FILE_BYTES_REQUEST")) {
            action = new FileBytesRequest(clientSocket, message);
        } else if (command.equals("FILE_BYTES_RESPONSE")) {
            action = new FileBytesResponse(clientSocket, message);
        }

        return action;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream(), "UTF-8"));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);

                Document message = Document.parse(inputLine);

                if (validateRequest(message)) {
                    Action action = getAction(message);
                    action.execute();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
