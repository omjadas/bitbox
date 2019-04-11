package unimelb.bitbox;

import java.util.LinkedList;
import java.util.Queue;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.HostPort;

public class ClientSearcher extends Thread {
	public static Queue<HostPort> potentialClients = new LinkedList<HostPort>();
	
	public ClientSearcher() {
		String clientList = Configuration.getConfigurationValue("peers");
		
		String[] clients = clientList.split(",");
		
		for (String client : clients) {
			String[] clientDetails = client.split(":");
			
			HostPort clientHostPort = new HostPort(clientDetails[0], Integer.parseInt(clientDetails[1]));
			potentialClients.add(clientHostPort);
		}
		
		this.start();
	}
	
	public synchronized void run() {
		while (!isInterrupted()) {
			while (ClientSearcher.potentialClients.size() == 0 || Client.establishedClients.size() == 10) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			HostPort potentialClient = potentialClients.remove();
			new Client(potentialClient.host, potentialClient.port);
		}
	}
}
