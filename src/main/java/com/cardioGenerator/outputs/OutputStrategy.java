package com.cardioGenerator.outputs;

/**
 * Defines a strategy for handling and outputting generated patient data.
 * Implementations of this interface determine how generated
 * data is processed for example printing to the console or writing to a file
 * 
 * @author Melanie Böhmer
 */
public interface OutputStrategy {
    /**
     * This method creates a single data record for a patients generated data. The implementation
     * defines how and where this data is delivered.
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the time at which the data was generated
     * @param label the type or category of the data (e.g., "HeartRate", "Saturation")
     * @param data the value of the measurement
     * @return This method does not return a value.
     * @throws NullPointerException if any required parameter is null depending on the implementation
     * 
     */
    void output(int patientId, long timestamp, String label, String data);
}
