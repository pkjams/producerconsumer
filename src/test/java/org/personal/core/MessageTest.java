package org.personal.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageTest {

    @Test
    public void verifyMessageValidInput() {
        Message message = new Message("ABCDEFGH".toCharArray(), 12345L, true);
        assertEquals("Message{fixedString=ABCDEFGH, longValue=12345, booleanValue=true}", message.toString());
    }

    @Test
    public void verifyMessageInValidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> new Message("ABCDEFGHI".toCharArray(), 12345L, true));
    }

    @Test
    public void verifyMessageInValidInput_2() {
        assertThrows(IllegalArgumentException.class,
                () -> new Message("ABCDEFG".toCharArray(), 12345L, true));
    }
}