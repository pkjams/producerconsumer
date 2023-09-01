package org.personal.core;

public class Message {

    private final char[] fixedString = new char[8];
    private final long longValue;
    private final boolean booleanValue;


    public Message(char[] fixedString, long longValue, boolean booleanValue) {
        if (fixedString.length != 8) {
            throw new IllegalArgumentException("fixedString must be exactly 8 characters");
        }
        System.arraycopy(fixedString, 0, this.fixedString, 0, 8);
        this.longValue = longValue;
        this.booleanValue = booleanValue;
    }

    @Override
    public String toString() {
        return "Message{" +
                "fixedString=" + String.valueOf(fixedString) +
                ", longValue=" + longValue +
                ", booleanValue=" + booleanValue +
                '}';
    }
}
