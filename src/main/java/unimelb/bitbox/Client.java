package unimelb.bitbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import unimelb.bitbox.actions.HandshakeRequest;

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
    	System.out.println("received");
    	this.clientSocket = socket;
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
    
    private void validateRequest() {
    	
    }

    public void run() {
    	try {
    		BufferedReader in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream(), "UTF-8"));
    		
    		String inputLine;
        	while ((inputLine = in.readLine()) != null) {
        		System.out.println(inputLine);
        	}    		
		} catch (IOException e) {
			e.printStackTrace();
		}   	
    }
}
