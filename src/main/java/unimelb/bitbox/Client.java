package unimelb.bitbox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.util.Base64;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import unimelb.bitbox.commands.AuthRequest;
import unimelb.bitbox.commands.Command;
import unimelb.bitbox.commands.ConnectPeerRequest;
import unimelb.bitbox.commands.DisconnectPeerRequest;
import unimelb.bitbox.commands.ListPeersRequest;
import unimelb.bitbox.util.CommandLineArgs;
import unimelb.bitbox.util.Document;

public class Client {
    private static Logger log = Logger.getLogger(Peer.class.getName());
    private static final String privateKeyFile = "bitboxclient_rsa";
    private static Socket socket;
    private static PrivateKey privateKey;
    private static SecretKeySpec aes;

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
            String peer = null;
            int peerPort = 0;

            if (!command.equals("list_peers")) {
                // Get peer and peer port
                peer = argsBean.getPeer();
                peerPort = argsBean.getPeerPort();
            }

            Client.privateKey = readPrivateKey();

            try {
                Client.socket = new Socket(server, serverPort);
                send(new AuthRequest(identity).getPayload());
            } catch (ConnectException e) {
                log.info("Could not connect to: " + server + ":" + serverPort);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(Client.socket.getInputStream(), "UTF-8"));
                Document incoming = Document.parse(in.readLine());
                if (incoming.getBoolean("status")) {
                    aes = decryptKey(privateKey, incoming.getString("AES128"));
                } else {
                    log.info("Peer does not have public key");
                    System.exit(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String payload = "";

            // if (command.equals("list_peers")) {
            //     payload = new ListPeersRequest().getPayload();
            // } else if (command.equals("connect_peer")) {
            //     payload = new ConnectPeerRequest(peer, (long) peerPort).getPayload();
            // } else if (command.equals("disconnect_peer")) {
            //     payload = new DisconnectPeerRequest().getPayload();
            // }

            // send(encrypt(payload));

        } catch (CmdLineException e) {

            System.err.println(e.getMessage());

            // Print the usage to help the user understand the arguments expected
            // by the program
            parser.printUsage(System.err);
        }
    }

    private static SecretKeySpec decryptKey(PrivateKey privateKey, String key) {
        try {
            System.out.println("decrypting");
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new SecretKeySpec(cipher.doFinal(Base64.getDecoder().decode(key)), "AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PrivateKey readPrivateKey() {
        Security.addProvider(new BouncyCastleProvider());
        try {
            PEMParser pemParser = new PEMParser(new FileReader(privateKeyFile));
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            Object object = pemParser.readObject();
            KeyPair kp = converter.getKeyPair((PEMKeyPair) object);
            pemParser.close();
            return kp.getPrivate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String encrypt(String payload) {
        return payload;
    }

    private static void send(String payload) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Client.socket.getOutputStream(), "UTF8"));
            out.write(payload);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            log.info("Socket was closed while sending message");
        }
    }
}
