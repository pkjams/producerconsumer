package org.personal.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Mian class to show the demo of producer and consumer making use of transmission to pass
 * Message
 */
public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        int NUMBER_OF_MESSAGES = 10_000;

        //create the API
        Transmission transmission = new Transmission(2048, executorService);


        //producer on its own thread to 'sent' the Message via transmission API
        Producer producer = new Producer(transmission,
                () -> new Message("12345678".toCharArray(), 10L, true),
                NUMBER_OF_MESSAGES);

        //consumer on its own thread to 'receive' the Message via transmission API
        Consumer consumer = new Consumer(transmission);

        executorService.submit(producer);
        executorService.submit(consumer);
    }
}