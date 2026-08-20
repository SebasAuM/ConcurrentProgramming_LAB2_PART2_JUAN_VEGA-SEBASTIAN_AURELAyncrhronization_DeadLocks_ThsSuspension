package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private boolean paused = false;
    private final Object pauseLock = new Object();
    private volatile boolean running = true;


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {

        while (running) {
            synchronized (pauseLock) {
                while (paused && running) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!running) {
                break;
            }
            
            //Agrego esta verificación para que el hilo termine cuando la salud del inmortal llegue a 0, 
            // de lo contrario el hilo no dejaria de ejecutarse y generaria errores
            if (this.getHealth() <= 0) {
                updateCallback.processReport(this + " is dead. Stopping thread.\n");
                break;
            }

            Immortal im;
            int myIndex = immortalsPopulation.indexOf(this);
            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);
            this.fight(im);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        updateCallback.processReport(this + " has stopped.\n");
    }

    public void stopImmortal() {
        running = false;
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll(); // por si estaba pausado esperando en wait()
        }
    }

    public void pause() {
        synchronized (pauseLock) {
            paused = true;
        }
    }
    
    public void resumeImmortal() {  
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    public void fight(Immortal i2) {
        Immortal first = this;
        Immortal second = i2;

        if (this.immortalsPopulation.indexOf(this) > this.immortalsPopulation.indexOf(i2)) {
            first = i2;
            second = this;
        }

        synchronized (first.pauseLock) {
            synchronized (second.pauseLock) {

                if (this.getHealth() <= 0) {
                    return;
                }
                if (i2.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.health += defaultDamageValue;
                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");

                    if (i2.getHealth() <= 0) {
                        immortalsPopulation.remove(i2);
                        updateCallback.processReport(i2 + " has died and was removed\n");
                    }
                } else {
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
            }
        }
    }
    
    public synchronized void changeHealth(int v) { 
        health = v;
    }
    
    public synchronized int getHealth() {  
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

}
