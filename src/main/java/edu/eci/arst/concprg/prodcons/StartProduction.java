/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class StartProduction {

    public static void main(String[] args) {

        Queue<Integer> queue = new LinkedBlockingQueue<>();

        new Producer(queue, 5).start();
        new Consumer(queue).start();
    }
}