package org.personal.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Transmission.MessageMuncher, Runnable {
    private static final Logger logger = LogManager.getLogger(Consumer.class);

    private final Transmission transmission;
    private final AtomicInteger atomicInteger;

    private volatile boolean terminateLoop;

    public Consumer(Transmission transmission) {
        this.transmission = transmission;
        this.atomicInteger = new AtomicInteger();
    }


    @Override
    public boolean on(Message m) {
        if (m != null) {
            int messageCount = atomicInteger.incrementAndGet();
            logger.info("{} Received {}", messageCount, m);
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
