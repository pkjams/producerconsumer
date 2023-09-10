package org.personal.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Producer implements Runnable {
    private static final Logger logger = LogManager.getLogger(Producer.class);

    private final Transmission transmission;
    private final int howManyMessages;

    private final Supplier<Message> messageSupplier;

    public Producer(Transmission transmission,
                    Supplier<Message> messageSupplier,
                    int howManyMessages) {
        if (transmission == null || messageSupplier == null) {
            throw new IllegalArgumentException("transmission and/or messageSupplier cannot be null");
        }
        if (howManyMessages <= 0) {
            throw new IllegalArgumentException("howManyMessages must be greater than zero");
        }
        this.transmission = transmission;
        this.messageSupplier = messageSupplier;
        this.howManyMessages = howManyMessages;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= howManyMessages; i++) {
                //ask supplier to give the message to be delivered.
                Message message = messageSupplier.get();

                // Send the message to the Transmission
                int result = transmission.write(message);

                if (result == 1) {
                    logger.debug("{} Wrote {}", i, message);
                } else {
                    logger.debug("{} Failed to write {}", i, message);
                }

                // Simulate some delay between producing messages
                TimeUnit.NANOSECONDS.sleep(0); // this works like no-op
            }
        } catch (InterruptedException e) {
            logger.warn("Caught  InterruptedException", e);
            Thread.currentThread().interrupt();
        }
    }
}
