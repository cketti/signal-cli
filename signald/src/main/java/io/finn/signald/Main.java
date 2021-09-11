package io.finn.signald;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.newsclub.net.unix.AFUNIXSocketCredentials;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.nio.file.Files;
import java.security.Security;

import io.finn.signald.clientprotocol.ClientConnection;
import io.finn.signald.util.SecurityProvider;

public class Main {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        // enable unlimited strength crypto via Policy, supported on relevant JREs
        Security.setProperty("crypto.policy", "unlimited");
        installSecurityProviderWorkaround();

        new Main().run();
    }

    private static void installSecurityProviderWorkaround() {
        // Register our own security provider
        Security.insertProviderAt(new SecurityProvider(), 1);
        Security.addProvider(new BouncyCastleProvider());
    }


    private String socketPath = "/var/run/signald/signald.sock";

    private void run() {
        try {
            // Spins up one thread per inbound connection to the control socket
            File socketFile = new File(socketPath);
            if (socketFile.exists()) {
                logger.debug("Deleting existing socket file");
                Files.delete(socketFile.toPath());
            }

            logger.info("Binding to socket " + socketPath);
            AFUNIXServerSocket server = AFUNIXServerSocket.newInstance();
            try {
                server.bind(new AFUNIXSocketAddress(socketFile));
            } catch (SocketException e) {
                logger.fatal("Error creating socket at " + socketFile + ": " + e.getMessage());
                System.exit(1);
            }

            while (!Thread.interrupted()) {
                try {
                    AFUNIXSocket socket = server.accept();
                    AFUNIXSocketCredentials credentials = socket.getPeerCredentials();
                    logger.debug("Connection from pid " + credentials.getPid() + " uid " + credentials.getUid());
                    new Thread(new ClientConnection(socket), "connection-pid-" + credentials.getPid()).start();
                } catch (IOException e) {
                    logger.catching(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
