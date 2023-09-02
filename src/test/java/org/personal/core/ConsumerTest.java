package org.personal.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

class ConsumerTest {
    @Mock
    private Transmission transmission;

    @Mock
    private Message message;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void verifyOnWithValidMessage() {
        // Arrange
        Consumer consumer = new Consumer(transmission);

        // Act
        boolean result = consumer.on(message);

        // Assert
        assertTrue(result);
    }

    @Test
    public void verifyOnWithNullMessage() {
        // Arrange
        Consumer consumer = new Consumer(transmission);

        // Act
        boolean result = consumer.on(null);

        // Assert
        assertFalse(result);
    }

    @Test
    public void verifyThatRunCallTransmissionRead() {
        // Arrange
        Consumer consumer = new Consumer(transmission);

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                consumer.terminateLoop();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        // Act
        consumer.run();

        // Assert
        // Verify that the run method calls transmission.read(10, this) as expected
        verify(transmission, atLeastOnce()).read(10, consumer);
    }
}