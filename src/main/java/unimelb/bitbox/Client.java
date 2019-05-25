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
            
            //Get command, server and server port
            String command = argsBean.getCommand();
            String server = argsBean.getServer();
            int serverPort = argsBean.getServerPort();
            
            if (!command.equals("list_peers")) {
                //Get peer and peer port
                String peer = argsBean.getPeer();
                int peerPort = argsBean.getPeerPort();
            
            }
            
        } catch (CmdLineException e) {
            
            System.err.println(e.getMessage());
            
            //Print the usage to help the user understand the arguments expected
            //by the program
            parser.printUsage(System.err);
        }
    
    }
    
    

}
