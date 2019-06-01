package unimelb.bitbox;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

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
import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.FileSystemManager.EVENT;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;
import unimelb.bitbox.util.GenericSocket;
import unimelb.bitbox.util.GenericSocketFactory;
import unimelb.bitbox.util.SchemaValidator;

public class RemotePeer extends Thread {
    private static Logger log = Logger.getLogger(RemotePeer.class.getName());
    public static Set<RemotePeer> establishedPeers = Collections
            .newSetFromMap(new ConcurrentHashMap<RemotePeer, Boolean>());
    private GenericSocket socket;
    private String host;
    private long port;
    private FileSystemManager fileSystemManager;

    private boolean isConnected = true;

    private boolean isIncomingConnection = false;

    private Set<Action> waitingActions;
    private JSONObject lastAction = null;

    public static HashMap<String, String> responseToRequest;
    public static HashSet<String> validCommandsBeforeConnectionEstablished;

    static {
        responseToRequest = new HashMap<>();

        responseToRequest.put("FILE_CREATE_RESPONSE", "FILE_CREATE_REQUEST");
        responseToRequest.put("FILE_DELETE_RESPONSE", "FILE_DELETE_REQUEST");
        responseToRequest.put("FILE_MODIFY_RESPONSE", "FILE_MODIFY_REQUEST");
        responseToRequest.put("DIRECTORY_CREATE_RESPONSE", "DIRECTORY_CREATE_REQUEST");
        responseToRequest.put("DIRECTORY_DELETE_RESPONSE", "DIRECTORY_DELETE_REQUEST");
        responseToRequest.put("FILE_BYTES_RESPONSE", "FILE_BYTES_REQUEST");

        validCommandsBeforeConnectionEstablished = new HashSet<>();

        validCommandsBeforeConnectionEstablished.add("HANDSHAKE_REQUEST");
        validCommandsBeforeConnectionEstablished.add("HANDSHAKE_RESPONSE");
        validCommandsBeforeConnectionEstablished.add("CONNECTION_REFUSED");
    }

    public RemotePeer(String host, int port, FileSystemManager fileSystemManager) {
        waitingActions = Collections.newSetFromMap(new ConcurrentHashMap<Action, Boolean>());
        setHost(host);
        setPort(port);
        this.fileSystemManager = fileSystemManager;
        try {
            this.socket = Peer.socketFactory.createOutgoingSocket(host, port);
            HandshakeRequest requestAction = new HandshakeRequest(this.socket,
                    Configuration.getConfigurationValue("advertisedName"),
                    Long.parseLong(Configuration.getConfigurationValue("port")), this);
            requestAction.send();
            this.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getIsConnected() {
        return this.isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public RemotePeer(GenericSocket socket, FileSystemManager fileSystemManager) {
        waitingActions = Collections.newSetFromMap(new ConcurrentHashMap<Action, Boolean>());
        this.socket = socket;
        this.fileSystemManager = fileSystemManager;

        if (RemotePeer.getNumberIncomingEstablishedConnections() == Peer.maximumIncommingConnections) {
            new ConnectionRefused(socket, "connection limit reached", this).send();
            return;
        }

        this.isIncomingConnection = true;
        this.start();
    }

    /**
     * Establish a connection with the remote peer
     */
    public void establishConnection() {
        establishedPeers.add(this);
    }
    
    public Set<Action> getWaitingActions() {
        return this.waitingActions;
    }

    public static int getNumberIncomingEstablishedConnections() {
        int numIncoming = 0;
        for (RemotePeer peer : RemotePeer.establishedPeers) {
            if (peer.isIncomingConnection()) {
                numIncoming++;
            }
        }

        return numIncoming;
    }

    public boolean isIncomingConnection() {
        return this.isIncomingConnection;
    }

    /**
     * Return the host of the remote peer
     * 
     * @return The host of the remote peer
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Return the port of the remote peer
     * 
     * @return The port of the remote peer
     */
    public long getPort() {
        return this.port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(long port) {
        this.port = port;
    }

    /**
     * Determine if the message is valid
     * 
     * @param message The received message
     * @return Boolean indication whether the message is valid
     */
    private boolean validateRequest(Document message) {
        if (!SchemaValidator.validateSchema(message)) {
            return false;
        }

        String command = message.getString("command");

        if (!RemotePeer.establishedPeers.contains(this)) {
            if (!validCommandsBeforeConnectionEstablished.contains(command)) {
                return false;
            }

            if (command == "HANDSHAKE_RESPONSE" || command == "CONNECTION_REFUSED") {
                return checkIfExpectingResponse(message);
            }
        } else {
            if (responseToRequest.containsKey(command)) {
                return checkIfExpectingResponse(message);
            } else if (!responseToRequest.containsValue(command)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkIfExpectingResponse(Document message) {
        for (Action action : waitingActions) {
            if (action.compare(message)) {
                waitingActions.remove(action);
                return true;
            }
        }

        return false;
    }

    public void addToWaitingActions(Action action) {
        waitingActions.add(action);
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
                    fileSystemEvent.pathName, this);
        } else if (fileSystemEvent.event == EVENT.FILE_DELETE) {
            action = new FileDeleteRequest(socket, new FileDescriptor(fileSystemEvent.fileDescriptor),
                    fileSystemEvent.pathName, this);
        } else if (fileSystemEvent.event == EVENT.FILE_MODIFY) {
            action = new FileModifyRequest(socket, new FileDescriptor(fileSystemEvent.fileDescriptor),
                    fileSystemEvent.pathName, this);
        } else if (fileSystemEvent.event == EVENT.DIRECTORY_CREATE) {
            action = new DirectoryCreateRequest(socket, fileSystemEvent.pathName, this);
        } else if (fileSystemEvent.event == EVENT.DIRECTORY_DELETE) {
            action = new DirectoryDeleteRequest(socket, fileSystemEvent.pathName, this);
        }

        action.send();
    }

    /**
     * Return an appropriate action for the received message
     * 
     * @param message The received message
     * @return An action corresponding to the received message
     */
    private Action getAction(Document message) {
        Action action = null;
        String command = message.getString("command");

        if (command.equals("INVALID_PROTOCOL")) {
            action = new InvalidProtocol(socket, message, this);
        } else if (command.equals("CONNECTION_REFUSED")) {
            action = new ConnectionRefused(socket, message, this);
        } else if (command.equals("HANDSHAKE_REQUEST")) {
            action = new HandshakeRequest(socket, message, this);
        } else if (command.equals("HANDSHAKE_RESPONSE")) {
            action = new HandshakeResponse(socket, message, this);
        } else if (command.equals("FILE_CREATE_REQUEST")) {
            action = new FileCreateRequest(socket, message, this);
        } else if (command.equals("FILE_CREATE_RESPONSE")) {
            action = new FileCreateResponse(socket, message, this);
        } else if (command.equals("FILE_DELETE_REQUEST")) {
            action = new FileDeleteRequest(socket, message, this);
        } else if (command.equals("FILE_DELETE_RESPONSE")) {
            action = new FileDeleteResponse(socket, message, this);
        } else if (command.equals("FILE_MODIFY_REQUEST")) {
            action = new FileModifyRequest(socket, message, this);
        } else if (command.equals("FILE_MODIFY_RESPONSE")) {
            action = new FileModifyResponse(socket, message, this);
        } else if (command.equals("DIRECTORY_CREATE_REQUEST")) {
            action = new DirectoryCreateRequest(socket, message, this);
        } else if (command.equals("DIRECTORY_CREATE_RESPONSE")) {
            action = new DirectoryCreateResponse(socket, message, this);
        } else if (command.equals("DIRECTORY_DELETE_REQUEST")) {
            action = new DirectoryDeleteRequest(socket, message, this);
        } else if (command.equals("DIRECTORY_DELETE_RESPONSE")) {
            action = new DirectoryDeleteResponse(socket, message, this);
        } else if (command.equals("FILE_BYTES_REQUEST")) {
            action = new FileBytesRequest(socket, message, this);
        } else if (command.equals("FILE_BYTES_RESPONSE")) {
            action = new FileBytesResponse(socket, message, this);
        }

        return action;
    }

    public void disconnect() {
        socket.disconnect(this);
    }

    public void run() {
        String inputLine;
        while (((inputLine = socket.receive()) != null) && (isConnected == true)) {

            // Check if action is a duplicate
            if (lastAction == Document.parse(inputLine).getObj()) {
                continue;
            }
            lastAction = Document.parse(inputLine).getObj();

            log.info("Received from " + getHost() + ":" + getPort() + ": " + inputLine);

            Document message = Document.parse(inputLine);

            if (validateRequest(message)) {
                Action action = getAction(message);
                action.execute(fileSystemManager);
            } else {
                Action invalid = new InvalidProtocol(socket, "Could not validate message:" + message.toJson(), this);
                invalid.send();
            }
        }
        log.info("Peer " + getHost() + ":" + getPort() + " has disconnected");

        RemotePeer.establishedPeers.remove(this);
        Peer.connectedPeers.remove(this);
        synchronized (Peer.getPeerSearchLock()) {
            Peer.getPeerSearchLock().notifyAll();
        }
    }
}
