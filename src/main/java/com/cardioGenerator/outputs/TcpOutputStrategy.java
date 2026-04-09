package com.cardioGenerator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Sends patient data over a TCP connection to a connected client.
 *
 * This class is intended to be used as an output strategy in the data simulation system.
 * It allows external systems to receive real-time patient data via a TCP connection. 
 * 
 * @author Melanie Böhmer 
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Constructs a new TcpOutputStrategy and starts a TCP server.
     *
     * @param port the port number on which the TCP server will listen;
     * @throws IllegalArgumentException if the port number is outside the valid range
     *
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method formats the provided patient data as a comma-separated string and
     * transmits it to the connected client via the TCP connection. 
     * The message format is: patientId,timestamp,label,data}
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the time of the measurement
     * @param label the type of data being sent (e.g."Saturation")
     * @param data the actual measurement value
     *
     * @return This method does not return a value.
     *
     * @throws IllegalStateException if the output stream is not initialized (i.e., no client connected)
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
