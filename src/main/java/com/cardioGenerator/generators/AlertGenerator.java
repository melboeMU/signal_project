package com.cardioGenerator.generators;//changed package name to lower lowerCamelcase 

import java.util.Random; //removed space between the two imports 
import com.cardioGenerator.outputs.OutputStrategy;

public class AlertGenerator implements PatientDataGenerator {

    public static final Random randomGenerator = new Random();
    // changed varibale name to lowerCamelCase here and on all following calls 
    private boolean[] alertStates; // false = resolved, true = pressed 

    public AlertGenerator(int patientCount) {
        // Added this. for clarity and consistency when assigning to a field.
        this.alertStates = new boolean[patientCount + 1];
    }

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
