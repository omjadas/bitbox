package unimelb.bitbox;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.logging.Logger;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import unimelb.bitbox.commands.AuthRequest;
import unimelb.bitbox.commands.Command;
import unimelb.bitbox.commands.ConnectPeerRequest;
import unimelb.bitbox.commands.DisconnectPeerRequest;
import unimelb.bitbox.commands.ListPeersRequest;
import unimelb.bitbox.util.CommandLineArgs;

public class Client {
    private static Logger log = Logger.getLogger(Peer.class.getName());
    private static Socket socket;

    public static void main(String[] args) {
        // Object that will store the parsed command line arguments
        CommandLineArgs argsBean = new CommandLineArgs();

        // Parser provided by args4j
        CmdLineParser parser = new CmdLineParser(argsBean);
        try {
            // Parse the arguments
            parser.parseArgument(args);

            // Get command, server and server port
            String command = argsBean.getCommand();
            String server = argsBean.getServer();
            int serverPort = argsBean.getServerPort();
            String identity = argsBean.getIdentity();

            if (!command.equals("list_peers")) {
                // Get peer and peer port
                String peer = argsBean.getPeer();
                int peerPort = argsBean.getPeerPort();
            }

            try {
                socket = new Socket(server, serverPort);
                send(new AuthRequest(identity).getPayload());
            } catch (ConnectException e) {
                log.info("Could not connect to: " + server + ":" + serverPort);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String payload = "";

            if (command.equals("list_peers")) {
                payload = new ListPeersRequest().getPayload();
            } else if (command.equals("connect_peer")) {
                payload = new ConnectPeerRequest().getPayload();
            } else if (command.equals("disconnect_peer")) {
                payload = new DisconnectPeerRequest().getPayload();
            }

            send(encrypt(payload));

        } catch (CmdLineException e) {

            System.err.println(e.getMessage());

            // Print the usage to help the user understand the arguments expected
            // by the program
            parser.printUsage(System.err);
        }
    }

    private static String encrypt(String payload) {
        return payload;
    }

    private static void send(String payload) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            out.write(payload);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            log.info("Socket was closed while sending message");
        }
    }
}
