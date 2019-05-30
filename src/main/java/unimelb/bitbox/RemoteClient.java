package unimelb.bitbox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.Arrays;

import unimelb.bitbox.commands.AuthResponse;
import unimelb.bitbox.commands.Command;
import unimelb.bitbox.commands.ConnectPeerRequest;
import unimelb.bitbox.commands.DisconnectPeerRequest;
import unimelb.bitbox.commands.ListPeersRequest;
import unimelb.bitbox.util.Document;

public class RemoteClient {
    Socket socket;
    SecretKeySpec aes;
    String identity;

    public RemoteClient(Socket socket) {
        this.socket = socket;
        auth();
        command();
    }

    /**
     * Complete the auth with the client
     */
    private void auth() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
            Document command = Document.parse(in.readLine());
            this.identity = command.getString("identity");

            // Check if the client is in authorized_keys
            if (ClientServer.authorized_keys.containsKey(this.identity)) {
                PublicKey publicKey = publicKey(ClientServer.authorized_keys.get(this.identity));
                this.aes = generateKey();
                send(new AuthResponse(encryptKey(publicKey), true, "public key found").getPayload());
            } else {
                send(new AuthResponse(null, false, "public key not found").getPayload());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void command() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
            Document payload = Document.parse(decrypt(Document.parse(in.readLine()).getString("payload")));

            Command toExecute = null;

            if (payload.getString("command").equals("LIST_PEERS_REQUEST")) {
                toExecute = new ListPeersRequest();
            } else if (payload.getString("command").equals("CONNECT_PEER_REQUEST")) {
                // toExecute = new ConnectPeerRequest();
            } else if (payload.getString("command").equals("DISCONNECT_PEER_REQUEST")) {
                // toExecute = new DisconnectPeerRequest();
            }

            Document doc = new Document();

            System.out.println(toExecute.execute());

            doc.append("payload", encrypt(toExecute.execute()));

            send(doc.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String encrypt(String payload) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aes);
            return Base64.getEncoder().encodeToString(cipher.doFinal(payload.getBytes()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String decrypt(String payload) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aes);
            return new String(cipher.doFinal(Base64.getDecoder().decode(payload)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Encode the AES key using the public key of the client
     * 
     * @param publicKey The key to use to encode the AES key
     * @return An encoded string of the AES key
     */
    private String encryptKey(PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(this.aes.getEncoded()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Generate a public key using the key from config
     * 
     * @param publicKey The string from the config to use
     * @return A PublicKey object generated from the key in config
     */
    private PublicKey publicKey(String publicKey) {
        byte[] publicBytes = Base64.getDecoder().decode(publicKey);
        BigInteger exponentLength = new BigInteger(Arrays.copyOfRange(publicBytes, 11, 15));
        BigInteger exponent = new BigInteger(Arrays.copyOfRange(publicBytes, 15, 15 + exponentLength.intValue()));
        BigInteger modulusLength = new BigInteger(
                Arrays.copyOfRange(publicBytes, 15 + exponentLength.intValue(), 19 + exponentLength.intValue()));
        BigInteger modulus = new BigInteger(Arrays.copyOfRange(publicBytes, 19 + exponentLength.intValue(),
                19 + exponentLength.intValue() + modulusLength.intValue()));

        try {
            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Generate a random AES key
     * 
     * @return Secret key to be used for commands
     */
    private SecretKeySpec generateKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[16];
        random.nextBytes(keyBytes);
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Send a string to the connected client
     * 
     * @param payload String to send to the client
     */
    private void send(String payload) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            out.write(payload);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}