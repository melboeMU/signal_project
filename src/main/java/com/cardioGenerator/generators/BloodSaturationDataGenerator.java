package com.cardioGenerator.generators;

import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;


/**
 * this calss Generates simulated blood oxygen saturation data for multiple patients.
 * Each patient is assigned an initial baseline saturation value between a valid range of 90% and 100%.
 * theState is maintained internally for each patient, meaning that each patient's saturation
 * evolves over time based on previously generated values.
 *
 * This class is intended to be used as part of a patient data simulation pipeline.
 * 
 * @author Melanie Böhmer 
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;

    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }
    

/**
 * Generates and outputs a simulated blood oxygen saturation value for a given patient.
 * This method updates the patient's last known saturation value by applying a small random
 * variation of -1, 0, or +1. The resulting value is then constrained to the valid range of 90% to 100%.
 *
 * @param patientId the unique identifier of the patient as a number 
 * @param outputStrategy the output mechanism used to show the generated data
 * @return This method does not return a value.
 * @throws ArrayIndexOutOfBoundsException if patientId is outside the valid range
 * @throws NullPointerException if outputStrategy is null
 *
 */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
