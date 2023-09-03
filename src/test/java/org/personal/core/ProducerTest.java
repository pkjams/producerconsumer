package org.personal.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProducerTest {

    @Mock
    private Transmission transmission;

    @Mock
    private Supplier<Message> messageSupplier;

    @Mock
    private Message message;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void verifyWriteSuccess() {
        // Arrange
        Producer producer = new Producer(transmission, messageSupplier, 1);

        when(messageSupplier.get()).thenReturn(message);
        when(transmission.write(message)).thenReturn(1);

        // Act
        producer.run();

        // Assert
        verify(transmission, times(1)).write(message);
    }

    @Test
    public void verifyWriteFailure() {
        // Arrange
        Producer producer = new Producer(transmission, messageSupplier, 1);

        when(messageSupplier.get()).thenReturn(message);
        when(transmission.write(message)).thenReturn(0);

        // Act
        producer.run();

        // Assert
        verify(transmission, times(1)).write(message);
    }

    @Test
    public void verifyWriteSuccessMultipleMessages() {
        // Arrange
        int numberOfMessages = 3;
        Producer producer = new Producer(transmission, messageSupplier, numberOfMessages);

        when(messageSupplier.get()).thenReturn(message);
        when(transmission.write(message)).thenReturn(1);

        // Act
        producer.run();

        // Assert
        verify(transmission, times(numberOfMessages)).write(message);
    }

    @Test
    public void verifyProducerWhenSupplierIsNull() {
        assertThrows(IllegalArgumentException.class,() -> new Producer(transmission, null, 1), "transmission and/or messageSupplier cannot be null");
    }

    @Test
    public void verifyProducerWhenTransmissionIsNull() {
        assertThrows(IllegalArgumentException.class,() -> new Producer(null, messageSupplier, 1), "transmission and/or messageSupplier cannot be null");
    }

}