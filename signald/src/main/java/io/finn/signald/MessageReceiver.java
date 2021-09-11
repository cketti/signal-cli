package io.finn.signald;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public class MessageReceiver {
  private static final Logger logger = LogManager.getLogger();

  public static void unsubscribeAll(final Socket socket) {
    logger.debug("MessageReceiver.unsubscribeAll()");
  }
}
