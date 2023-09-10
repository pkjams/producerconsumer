package org.personal.core;

public class Message {

    private final char[] fixedString = new char[8];
    private long longValue;
    private boolean booleanValue;


    public void setMessage(char[] fixedString, long longValue, boolean booleanValue) {
        if (fixedString == null || fixedString.length != 8) {
            throw new IllegalArgumentException("fixedString must be exactly 8 characters");
        }
        System.arraycopy(fixedString, 0, this.fixedString, 0, 8);
        this.longValue = longValue;
        this.booleanValue = booleanValue;
    }

    public void fromMessage(Message message) {
        setMessage(message.fixedString, message.longValue, message.booleanValue);
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
