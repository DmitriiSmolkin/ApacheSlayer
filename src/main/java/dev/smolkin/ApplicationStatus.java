package dev.smolkin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public enum ApplicationStatus {

    INSTANCE;

    private int activeConnections;
    private int closedConnections;
    private int averageConnectionLifetime = 0; // in seconds
    private List<Socket> openedSockets = new ArrayList<>();

    public synchronized void addSocket(Socket socket) {
        openedSockets.add(socket);
    }

    public synchronized void removeSocket(Socket socket) {
        openedSockets.remove(socket);
    }

    public synchronized int getOpenedSockets() {
        return openedSockets.size();
    }

    public synchronized int getActiveConnections() {
        return activeConnections;
    }

    public synchronized int getClosedConnections() { return closedConnections; }

    public synchronized void addActiveConnection() {
        this.activeConnections++;
    }

    public synchronized void closeConnection() {
        this.activeConnections--;
        this.closedConnections++;
    }

    public synchronized void addLifeTime(long nanoseconds) {
        this.averageConnectionLifetime = (int) ((this.averageConnectionLifetime + (nanoseconds / (int) 1e9)) / 2);
    }

    public synchronized int getAverageConnectionLifetime() {
        return this.averageConnectionLifetime;
    }

    // TODO: remove synchronized where it isn't needed

    public boolean isServiceAvailable(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }

    }

}
