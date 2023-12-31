package org.personal.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Consumer implements Transmission.MessageMuncher, Runnable {
    private static final Logger logger = LogManager.getLogger(Consumer.class);

    private final Transmission transmission;

    private volatile boolean terminateLoop;

    public Consumer(Transmission transmission) {
        if (transmission == null) {
            throw new IllegalArgumentException("transmission cannot be null");
        }
        this.transmission = transmission;
    }


    @Override
    public boolean on(Message m) {
        if (m != null) {
            logger.info("Received {}", m);
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        while (!terminateLoop) {
            transmission.read(10, this); // request up to 10 msgs from transmission
        }
    }

    public void terminateLoop() {
        this.terminateLoop = true;
    }

}
