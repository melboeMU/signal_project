package com.cardioGenerator.generators;//changed package name to lower lowerCamelcase 

import java.util.Random; //removed space between the two imports 
import com.cardioGenerator.outputs.OutputStrategy;


/** 
 * This class simulates alert states for patients, where each patient can either
 * have an active alert or no alert. Alerts are triggered based on a probabilistic
 * model and resolved over time with a defined likelihood. 
 * 
 * @author Melanie Böhmer 
 */
public class AlertGenerator implements PatientDataGenerator {

    public static final Random randomGenerator = new Random();
    // changed varibale name to lowerCamelCase here and on all following calls 
    private boolean[] alertStates; // false = resolved, true = pressed 

    /**
     * Constructs a new AlertGenerator for a given number of patients.
     * Each patient is initialized with no active alert. 
     *
     * @param patientCount the number of patients
     *
     * @throws NegativeArraySizeException if patientCount is negative
     */

    public AlertGenerator(int patientCount) {
        // Added this. for clarity and consistency when assigning to a field.
        this.alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates and outputs an alert event for a given patient.
     *
     * This method simulates the lifecycle of an alert for a patient. If an alert is
     * currently active, there is a high probability (90%) that it will be resolved.
     * If no alert is active, a new alert may be triggered based on a probabilistic
     * model using an exponential distribution approximation.
     *
     * @param patientId the unique identifier of the patient
     * @param outputStrategy the output mechanism used to handle the generated alert
     *
     * @return This method does not return a value
     *
     * @throws ArrayIndexOutOfBoundsException if {@code patientId} is outside the valid range
     * @throws NullPointerException if {@code outputStrategy} is {@code null}
     */

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                //changed variable Lambda to lower case 
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    
                    // removed comment here because it is self-explanatory what is happening 
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
