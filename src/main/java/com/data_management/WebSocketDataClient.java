package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * WebSocket client that connects to the signal generator WebSocket server.
 *
 * It receives real-time patient data, parses each message, and stores valid
 * records in DataStorage.
 */
public class WebSocketDataClient extends WebSocketClient {

    private final DataStorage dataStorage;

    public WebSocketDataClient(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.dataStorage = dataStorage;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        // This confirms that the client successfully connected to the server.
        System.out.println("Connected to WebSocket server: " + getURI());
    }

    @Override
    public void onMessage(String message) {
        /*
         * Incoming messages should have this format:
         * patientId,timestamp,label,data
         *
         * Example:
         * 1,1714900000000,HeartRate,82.0
         */
        try {
            parseAndStoreMessage(message);
        } catch (IllegalArgumentException e) {
            /*
             * Invalid messages should not crash the application.
             * Instead, they are logged and ignored.
             */
            System.err.println("Ignoring corrupted WebSocket message: " + message);
            System.err.println("Reason: " + e.getMessage());
        }
    }

    private void parseAndStoreMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message is empty");
        }

        String[] parts = message.split(",", 4);

        if (parts.length != 4) {
            throw new IllegalArgumentException(
                    "Expected 4 fields: patientId,timestamp,label,data"
            );
        }

        int patientId;
        long timestamp;
        double measurementValue;
        String recordType;

        try {
            patientId = Integer.parseInt(parts[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid patient ID", e);
        }

        try {
            timestamp = Long.parseLong(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid timestamp", e);
        }

        recordType = parts[2].trim();

        if (recordType.isEmpty()) {
            throw new IllegalArgumentException("Record type is empty");
        }

        try {
            measurementValue = Double.parseDouble(parts[3].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid measurement value", e);
        }

        /*
         * Store the parsed data in DataStorage.
         * DataStorage is responsible for updating existing patients or creating
         * new patient records if the patient does not exist yet.
         */
        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        /*
         * This handles interruptions in the data stream.
         * The connection may close because the server stopped, the network failed,
         * or the client was manually closed.
         */
        System.out.println("WebSocket connection closed.");
        System.out.println("Code: " + code + ", Reason: " + reason + ", Remote: " + remote);
    }

    @Override
    public void onError(Exception ex) {
        /*
         * Network or transmission errors are handled here.
         * The program logs the error instead of crashing immediately.
         */
        System.err.println("WebSocket error occurred:");
        ex.printStackTrace();
    }
}