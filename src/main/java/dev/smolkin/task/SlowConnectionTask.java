package dev.smolkin.task;

import dev.smolkin.ApplicationStatus;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class SlowConnectionTask implements Runnable {

    private final String host;
    private final int    port;

    private final ApplicationStatus status = ApplicationStatus.INSTANCE;

    public SlowConnectionTask(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {

        long startTime = System.nanoTime();

        Socket socket = null;

        try {
            socket = new Socket(this.host, this.port);
            status.addSocket(socket);
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
                writer.write("GET / HTTP/1.1\r\n");
                writer.write("Host: " + this.host + "\r\n");
                writer.write("Connection: keep-alive\r\n");
                writer.flush();
                status.addActiveConnection();
                for (int i = 0; i < 1e8; i++) {
                    writer.write("X-a: a\r\n");
                    writer.flush();
                    Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3000));
                }

                //writer.write("\r\n");
                //writer.flush();

                //long estimatedTime = System.nanoTime() - startTime;
                //status.closeConnection();
                //status.addLifeTime(estimatedTime);
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        System.out.println(line);
//                    }
//                }


            }
        } catch (InterruptedException | IOException e) {
            long estimatedTime = System.nanoTime() - startTime;
            status.closeConnection();
            status.removeSocket(socket);
            status.addLifeTime(estimatedTime);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
