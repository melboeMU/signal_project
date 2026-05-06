package com.data_management;

import java.io.IOException;

public interface DataReader {
    /**
     * Reads data from a specified source and stores it in the data storage.
     * 
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Stops reading data from the source.
     *
     * This is necessary for real-time sources such as WebSockets because they
     * continue listening for new data until the connection is closed.
     *
     * The default implementation is empty so existing file-based DataReader
     * classes do not have to implement this method immediately.
     *
     * @throws IOException if there is an error while stopping the reader
     */
    default void stopReading() throws IOException {
        // No action by default, since file readers finish after reading the file
    }
}
