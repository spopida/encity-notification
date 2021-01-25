package uk.co.encity.notification.service;

import org.springframework.stereotype.Component;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
public class Receiver {
    private final Logger logger = Loggers.getLogger(getClass());

    public void receiveMessage(String message) {
        logger.debug("Received <" + message + ">");
    }
}
