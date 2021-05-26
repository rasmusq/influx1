package com.ralleq.influx.objects;

public abstract class TickHandler {

    private Thread tickThread;

    private long THREAD_TIME = 8333333, lastTime, now;
    private double delta;
    private boolean keepLoopAlive = true, ticking = true;

    public abstract void tick();
    public TickHandler() {

        tickThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while(keepLoopAlive) {
                    if(ticking) {
                        now = System.nanoTime();

                        delta += (double)(now - lastTime)/(double)THREAD_TIME;

                        if(delta > 1) {
                            tick();
                            delta = 0;
                        }

                        lastTime = now;
                    }
                }
            }
        });
        tickThread.setName("Tick Thread");
        tickThread.start();

    }

}
