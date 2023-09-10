package org.personal.core;

import java.util.function.Supplier;

public class MessageSupplier implements Supplier<Message> {
    private final Message message = new Message();
    private long count;

    @Override
    public Message get() {
        message.setMessage("12345678".toCharArray(), ++count, true);
        return message;
    }
}
