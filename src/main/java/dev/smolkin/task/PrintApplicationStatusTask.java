package dev.smolkin.task;

import dev.smolkin.ApacheSlayer;
import dev.smolkin.ApplicationStatus;

public class PrintApplicationStatusTask implements Runnable {

    private String host;
    private int    port;
    private int timeout; // milliseconds
    private long startTime;
    private int duration; // seconds

    private ApplicationStatus status = ApplicationStatus.INSTANCE;

    public PrintApplicationStatusTask(String host, int port, int timeout, long startTime, int duration) {
        this.host    = host;
        this.port    = port;
        this.timeout = timeout;
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public void run() {

        System.out.print("\rActive/Closed connections: " + status.getActiveConnections() + "/" + status.getClosedConnections() +
                " Average connection lifetime: " + status.getAverageConnectionLifetime() + "s" +
                " Time: " + getApplicationLifetime() +
                " Service available: " + status.isServiceAvailable(host, port, timeout) +
                " Memory usage: " + ApacheSlayer.getMemoryUsage() +
                " Sockets: " + status.getOpenedSockets());

    }

    private int getApplicationLifetime() {
        return (int) ((System.nanoTime() - startTime) / (int) 1e9);
    }


}
