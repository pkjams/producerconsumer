package org.personal.core;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main API which make use of lmax disruptor to safely pass Message from write to read.
 */
public class Transmission {

    private final RingBuffer<Message> ringBuffer;

    private final MessageEventHandler messageEventHandler;

    public Transmission(int bufferSize, ExecutorService executorService) {
        this(RingBuffer.createSingleProducer(
                        Message::new, bufferSize, new YieldingWaitStrategy()),
                new MessageEventHandler(), executorService);

    }

    Transmission(RingBuffer<Message> ringBuffer,
                 MessageEventHandler messageEventHandler,
                 ExecutorService executorService) {
        this.ringBuffer = ringBuffer;
        this.messageEventHandler = messageEventHandler;
        BatchEventProcessor<Message> batchEventProcessor = new BatchEventProcessor<>(ringBuffer, ringBuffer.newBarrier(), messageEventHandler);
        ringBuffer.addGatingSequences(batchEventProcessor.getSequence());
        executorService.submit(batchEventProcessor);
    }

    public synchronized int write(Message m) {
        if (m == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        long sequence = ringBuffer.next();
        try {
            Message message = ringBuffer.get(sequence);
            message.fromMessage(m);
        } finally {
            ringBuffer.publish(sequence);
        }
        return 1; // Successfully added one message to the Disruptor
    }


    public void read(int howMany, MessageMuncher m) {
        messageEventHandler.addMessagesToRead(howMany);
        messageEventHandler.setMessageMuncher(m);
    }

    public interface MessageMuncher {

        boolean on(Message m);

    }

    static class MessageEventHandler implements EventHandler<Message> {
        private final AtomicInteger remainingMessages = new AtomicInteger(0);
        private volatile MessageMuncher messageMuncher;

        @Override
        public void onEvent(Message message, long sequence, boolean endOfBatch) {
            if (remainingMessages.get() > 0) {
                if (message != null) {
                    boolean processed = messageMuncher.on(message);
                    if (processed) {
                        remainingMessages.decrementAndGet();
                    }
                }
            }
        }

        public void addMessagesToRead(int howMany) {
            this.remainingMessages.addAndGet(howMany);
        }

        public void setMessageMuncher(MessageMuncher messageMuncher) {
            this.messageMuncher = messageMuncher;
        }
    }
}
