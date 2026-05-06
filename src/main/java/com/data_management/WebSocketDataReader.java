package com.data_management;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * DataReader implementation for real-time WebSocket data.
 *
 * Instead of reading from a static file, this reader connects to a WebSocket
 * server and continuously stores incoming patient data in DataStorage.
 */
public class WebSocketDataReader implements DataReader {

    private final String serverUrl;
    private WebSocketDataClient client;

    public WebSocketDataReader(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        try {
            /*
             * Create the WebSocket client.
             * The client receives messages asynchronously through onMessage().
             */
            client = new WebSocketDataClient(new URI(serverUrl), dataStorage);

            /*
             * connect() starts the connection in the background.
             * This is necessary because WebSocket data arrives continuously.
             */
            client.connect();

        } catch (URISyntaxException e) {
            throw new IOException("Invalid WebSocket URL: " + serverUrl, e);
        }
    }

    @Override
    public void stopReading() throws IOException {
        if (client != null) {
            /*
             * Closing the client is necessary to stop listening for new data
             * and to release network resources.
             */
            client.close();
        }
    }
}