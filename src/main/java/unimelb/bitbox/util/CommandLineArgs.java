package unimelb.bitbox.util;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.CmdLineException;

public class CommandLineArgs {
    
    private String command;
    
    @Option(required = true, name = "-s", usage = "Server")
    private String server;
    
    @Option(required = false, name = "-p", usage = "Peer")
    private String peer;
    
    @Option(required = false, name = "-i", usage = "Identity")
    private String identity;
    
    @Option(required = true, name = "-c", usage = "Command")
    private void setCommand(String command) throws CmdLineException {
        if (command.equals("list_peers") || command.equals("connect_peer") || command.equals("disconnect_peer")) {
            this.command = command;
        } else {
            throw new CmdLineException(null, "Command is not recognised", null);
        }
    }
    
    public String getCommand() throws CmdLineException {
        
        if (!command.equals("list_peers") && this.peer == null) {
            throw new CmdLineException(null, "Peer must be provided", null);
        }
        
        return command;
    }
    
    public String getServer()  {
        return server.split(":")[0];
    }
    
    public int getServerPort() {
        return Integer.parseInt(server.split(":")[1]);   
    }
    
    public String getPeer() {
        return peer.split(":")[0];
    }
    
    public int getPeerPort() {
        return Integer.parseInt(peer.split(":")[1]);
    }
    
    public String getIdentity() {
        return identity;
    }
    
}
