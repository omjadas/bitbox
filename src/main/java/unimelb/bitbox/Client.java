package unimelb.bitbox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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

            // Connect to peer
            try {
                Client.socket = new Socket(server, serverPort);
                // Send AUTH_REQUEST to peer
                send(new AuthRequest(identity).getPayload());
            } catch (IOException e) {
                log.info("Could not connect to: " + server + ":" + serverPort);
                System.exit(0);
            }

            // Receive AUTH_RESPONSE from peer
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
                log.info("Unable to complete challenge response with peer");
                System.exit(0);
            }

            String payload = "";

            if (command.equals("list_peers")) {
                payload = new ListPeersRequest().getPayload();
            } else if (command.equals("connect_peer")) {
                payload = new ConnectPeerRequest(peer, (long) peerPort).getPayload();
            } else if (command.equals("disconnect_peer")) {
                payload = new DisconnectPeerRequest(peer, (long) peerPort).getPayload();
            }

            Document doc = new Document();
            doc.append("payload", encrypt(payload));

            // Send request to peer
            send(doc.toJson());

            // Receive response from peer
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(Client.socket.getInputStream(), "UTF-8"));
                System.out.println(decrypt(Document.parse(in.readLine()).getString("payload")));
            } catch (IOException e) {
                log.info("Unable to process response payload");
                System.exit(0);
            }

        } catch (CmdLineException e) {
            System.err.println(e.getMessage());

            // Print the usage to help the user understand the arguments expected
            // by the program
            parser.printUsage(System.err);
        }
    }

    /**
     * Decrypt an AES key using a private key
     * 
     * @param privateKey private key to decrypt the AES key with
     * @param key        base64 encoded AES key
     * @return SecretKeySpec created from decrypted AES key
     */
    private static SecretKeySpec decryptKey(PrivateKey privateKey, String key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new SecretKeySpec(cipher.doFinal(Base64.getDecoder().decode(key)), "AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            log.info("Unable to decrypt aes key");
            System.exit(0);
        }
        return null;
    }

    /**
     * Read a private key from a file
     * 
     * @return PrivateKey created from file
     */
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
            log.info("Unable to read private key from file");
            System.exit(0);
        }
        return null;
    }

    /**
     * Encrypt a string using the secret key
     * 
     * @param payload String to encrypt
     * @return Encrypted string
     */
    private static String encrypt(String payload) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, Client.aes);
            return Base64.getEncoder().encodeToString(cipher.doFinal(payload.getBytes()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            log.info("Unable to encrypt request payload");
            System.exit(0);
        }
        return null;
    }

    /**
     * Decrypt a string using the secret key
     * 
     * @param payload String to decrypt
     * @return Decrypted string
     */
    private static String decrypt(String payload) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, Client.aes);
            return new String(cipher.doFinal(Base64.getDecoder().decode(payload)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            log.info("Unable to decrypt response payload");
            System.exit(0);
        }
        return null;
    }

    /**
     * Send a string to the peer
     * 
     * @param payload String to send to the peer
     */
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
