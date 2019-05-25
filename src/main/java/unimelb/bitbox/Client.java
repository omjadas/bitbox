package unimelb.bitbox;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import unimelb.bitbox.util.CommandLineArgs;

public class Client {

    public static void main(String[] args) {
        //Object that will store the parsed command line arguments
        CommandLineArgs argsBean = new CommandLineArgs();
        
        //Parser provided by args4j
        CmdLineParser parser = new CmdLineParser(argsBean);
        try {
            
            //Parse the arguments
            parser.parseArgument(args);
            
            String command = argsBean.getCommand();
            String server = argsBean.getServer();
            int serverPort = argsBean.getServerPort();
            
            //After parsing, the fields in argsBean have been updated with the given
            //command line arguments
            
            System.out.println("Command: " + command);
            System.out.println("Server: " + server);
            System.out.println("Server Port: " + serverPort);
           
            if (!command.equals("list_peers")) {
                
                String peer = argsBean.getPeer();
                int peerPort = argsBean.getPeerPort();
                
                System.out.println("Peer: " + peer);
                System.out.println("Peer Port: " + peerPort);
            
            }
            
        } catch (CmdLineException e) {
            
            System.err.println(e.getMessage());
            
            //Print the usage to help the user understand the arguments expected
            //by the program
            parser.printUsage(System.err);
        }
    
    }
    
    

}
