package org.personal.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageTest {
    
    private Message message = new Message();

    @Test
    public void verifyMessageValidInput() {
        message.setMessage("ABCDEFGH".toCharArray(), 12345L, true);
        assertEquals("Message{fixedString=ABCDEFGH, longValue=12345, booleanValue=true}", message.toString());
    }

    @Test
    public void verifyMessageInValidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> message.setMessage("ABCDEFGHI".toCharArray(), 12345L, true));
    }

    @Test
    public void verifyMessageInValidInput_2() {
        assertThrows(IllegalArgumentException.class,
                () -> message.setMessage("ABCDEFG".toCharArray(), 12345L, true));
    }

    @Test
    public void verifyMessageNull() {
        assertThrows(IllegalArgumentException.class,
                () -> message.setMessage(null, 12345L, true));
    }

    @Test
    public void verifyMessageEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> message.setMessage(new char[]{}, 12345L, true));
    }
}