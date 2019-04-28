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
import unimelb.bitbox.util.FileSystemManager.EVENT;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;
import unimelb.bitbox.util.SchemaValidator;
import unimelb.bitbox.FileDescriptor;

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
        establishConnection();
        this.start();
    }

    /**
     * Establish a connection with the client
     */
    public void establishConnection() {
        if (establishedClients.size() == Peer.maximumIncommingConnections) {
            new ConnectionRefused(socket, "connection limit reached").send();
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
    	return SchemaValidator.validateSchema(message);
    }

    /**
     * Create corresponding actions for each FileSystemEvent
     * 
     * @param fileSystemEvent The FileSystemEvent to process
     */
    public void processEvent(FileSystemEvent fileSystemEvent) {
        Action action = null;

        if (fileSystemEvent.event == EVENT.FILE_CREATE) {
            action = new FileCreateRequest(socket, new FileDescriptor(fileSystemEvent.fileDescriptor),
                    fileSystemEvent.pathName);
        } else if (fileSystemEvent.event == EVENT.FILE_DELETE) {
            action = new FileDeleteRequest(socket, new FileDescriptor(fileSystemEvent.fileDescriptor),
                    fileSystemEvent.pathName);
        } else if (fileSystemEvent.event == EVENT.FILE_MODIFY) {
            action = new FileModifyRequest(socket, new FileDescriptor(fileSystemEvent.fileDescriptor),
                    fileSystemEvent.pathName);
        } else if (fileSystemEvent.event == EVENT.DIRECTORY_CREATE) {
            action = new DirectoryCreateRequest(socket, fileSystemEvent.pathName);
        } else if (fileSystemEvent.event == EVENT.DIRECTORY_DELETE) {
            action = new DirectoryDeleteRequest(socket, fileSystemEvent.pathName);
        }

        action.send();
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

        if (command.equals("INVALID_PROTOCOL")) {
            action = new InvalidProtocol(socket, message);
        } else if (command.equals("CONNECTION_REFUSED")) {
            action = new ConnectionRefused(socket, message);
        } else if (command.equals("HANDSHAKE_REQUEST")) {
            action = new HandshakeRequest(socket, message);
        } else if (command.equals("HANDSHAKE_RESPONSE")) {
            action = new HandshakeResponse(socket, message);
            establishedClients.add(this);
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
