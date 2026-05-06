package com.app;

import com.data_management.DataStorage;
import com.data_management.WebSocketDataReader;

public class RealTimeTestApp {

    /**
     * RealTimeTestApp is a simple standalone application used to verify that
     * real-time data streaming via WebSockets is working correctly.
     *
     * It connects to the WebSocket server (started by the data generator),
     * continuously receives incoming patient data, stores it in DataStorage,
     * and prints the stored data periodically to the console.
     *
     * This class is mainly used for testing and demonstration purposes,
     * allowing developers to observe the full data flow in real time.
     * 
     * @param args command-line arguments
     * @throws Exception if there is an error during WebSocket connection or data handling
    */
    public static void main(String[] args) throws Exception {

        DataStorage storage = DataStorage.getInstance();

        // Create a WebSocketDataReader that connects to the local WebSocket server
        // running on port 8887
        WebSocketDataReader reader =
                new WebSocketDataReader("ws://localhost:8887");

        reader.readData(storage);

        System.out.println("Listening for real-time data...\n");

        // Infinite loop to periodically display the stored data
        // This allows us to observe how data updates in real time
        // Print data every 2 seconds
        while (true) {
            Thread.sleep(2000);

            storage.getAllPatients().forEach(patient -> {
                System.out.println("Patient: " + patient.getPatientId());
                storage.getRecords(patient.getPatientId(), 0, Long.MAX_VALUE)
                        .forEach(record ->
                                System.out.println("  " + record.getRecordType()
                                        + " = " + record.getMeasurementValue()
                                        + " @ " + record.getTimestamp()));
            });

            //// Separator for better readability in the console output
            System.out.println("----");
        }
    }
}
