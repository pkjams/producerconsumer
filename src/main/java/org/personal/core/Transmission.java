package org.personal.core;

import com.lmax.disruptor.*;

import java.util.concurrent.ExecutorService;

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

    public int write(Message m) {
        if (m == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        ringBuffer.publishEvent(eventTranslator, m);
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
        private MessageMuncher messageMuncher;
        private int remainingMessages;

        @Override
        public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) {
            if (remainingMessages > 0) {
                Message message = event.getMessage();
                if (message != null) {
                    boolean processed = messageMuncher.on(message);
                    if (processed) {
                        remainingMessages--;
                    }
                }
            }
        }

        public void addMessagesToRead(int howMany) {
            this.remainingMessages += howMany;
        }

        public void setMessageMuncher(MessageMuncher messageMuncher) {
            this.messageMuncher = messageMuncher;
        }
    }
}
