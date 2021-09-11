package io.finn.signald.clientprotocol.v1;

import org.asamk.signal.manager.Manager;
import org.asamk.signal.manager.config.ServiceEnvironment;
import org.asamk.signal.manager.storage.identities.TrustNewIdentity;

import java.io.File;

public class Utils {

    public static Manager getManager(final String username) throws Exception {
        var settingsPath = new File(getDataHomeDir(), "signal-cli");
        var serviceEnvironment = ServiceEnvironment.LIVE;
        var userAgent = "Signal-Android/5.22.3 signald/test";
        var trustNewIdentity = TrustNewIdentity.ON_FIRST_USE;

        return Manager.init(username, settingsPath, serviceEnvironment, userAgent, trustNewIdentity);
    }

    private static File getDataHomeDir() {
        var dataHome = System.getenv("XDG_DATA_HOME");
        if (dataHome != null) {
            return new File(dataHome);
        }

        return new File(new File(System.getProperty("user.home"), ".local"), "share");
    }
}
