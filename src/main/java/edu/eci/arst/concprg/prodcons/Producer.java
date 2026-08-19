/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.Random;

/**
 *
 * @author hcadavid
 */
public class Producer extends Thread {

    private Queue<Integer> queue = null;
    private int dataSeed = 0;
    private Random rand = null;
    private final long stockLimit;

    public Producer(Queue<Integer> queue, long stockLimit) {
        this.queue = queue;
        rand = new Random(System.currentTimeMillis());
        this.stockLimit = stockLimit;
    }

    @Override
    public void run() {
        while (true) {
            dataSeed = dataSeed + rand.nextInt(100);

            synchronized (queue) {
                while (queue.size() >= stockLimit) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                queue.add(dataSeed);
                System.out.println("Producer added " + dataSeed);
                queue.notifyAll();
            }
        }
    }
}
