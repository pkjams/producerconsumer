package org.personal.core;

import com.lmax.disruptor.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main API which make use of lmax disruptor to safely pass Message from write to read.
 */
public class Transmission {

    private final RingBuffer<MessageEvent> ringBuffer;
    private final EventTranslatorOneArg<MessageEvent, Message> eventTranslator;

    private final MessageEventHandler messageEventHandler;

    public Transmission(int bufferSize, ExecutorService executorService) {
        this(RingBuffer.createSingleProducer(
                        MessageEvent::new, bufferSize, new YieldingWaitStrategy()),
                (event, sequence, arg0) -> event.setMessage(arg0),
                new MessageEventHandler(), executorService);

    }

    Transmission(RingBuffer<MessageEvent> ringBuffer,
                 EventTranslatorOneArg<MessageEvent, Message> eventTranslator,
                 MessageEventHandler messageEventHandler,
                 ExecutorService executorService) {
        this.ringBuffer = ringBuffer;
        this.eventTranslator = eventTranslator;
        this.messageEventHandler = messageEventHandler;
        BatchEventProcessor<MessageEvent> batchEventProcessor = new BatchEventProcessor<>(ringBuffer, ringBuffer.newBarrier(), messageEventHandler);
        ringBuffer.addGatingSequences(batchEventProcessor.getSequence());
        executorService.submit(batchEventProcessor);
    }

    public synchronized int write(Message m) {
        if (m == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        long sequence = ringBuffer.next();
        try {
            MessageEvent event = ringBuffer.get(sequence);
            eventTranslator.translateTo(event, sequence, m);
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

    static class MessageEventHandler implements EventHandler<MessageEvent> {
        private volatile MessageMuncher messageMuncher;
        private AtomicInteger remainingMessages = new AtomicInteger(0);

        @Override
        public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) {
            if (remainingMessages.get() > 0) {
                Message message = event.getMessage();
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
