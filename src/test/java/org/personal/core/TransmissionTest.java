package org.personal.core;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TransmissionTest {

    @Mock
    private RingBuffer<MessageEvent> ringBuffer;

    @Mock
    private EventTranslatorOneArg<MessageEvent, Message> eventTranslator;

    @Mock
    private Transmission.MessageEventHandler messageEventHandler;

    @Mock
    private Message message;

    private Transmission transmission;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        transmission = new Transmission(ringBuffer, eventTranslator, messageEventHandler, Executors.newFixedThreadPool(1));
    }

    @Test
    public void verifyValidMessageWrite() {
        // Act
        int result = transmission.write(message);

        // Assert
        assertEquals(1, result);
        verify(ringBuffer, times(1)).publishEvent(eventTranslator, message);
    }

    @Test
    public void verifyNullMessageWrite() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> transmission.write(null));
    }

    @Test
    public void verifyRead() {
        // Arrange
        Transmission.MessageMuncher messageMuncher = mock(Transmission.MessageMuncher.class);
        int howMany = 5;

        // Act
        transmission.read(howMany, messageMuncher);

        // Assert
        verify(messageEventHandler, times(1)).addMessagesToRead(howMany);
        verify(messageEventHandler, times(1)).setMessageMuncher(messageMuncher);
    }

    @Test
    public void verifyOnEvent() throws Exception {
        // Arrange
        MessageEvent event = new MessageEvent();
        event.setMessage(message);
        Transmission.MessageMuncher messageMuncher = mock(Transmission.MessageMuncher.class);
        Transmission.MessageEventHandler messageEventHandler = new Transmission.MessageEventHandler();
        messageEventHandler.setMessageMuncher(messageMuncher);
        messageEventHandler.addMessagesToRead(1);

        // Act
        messageEventHandler.onEvent(event, 1, true);

        // Assert
        verify(messageMuncher, times(1)).on(message);
    }
}