package com.cardioGenerator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Defines a contract for generating simulated patient data.
 *
 * This interface represents a generic data generator within the health data
 * simulation system. it is used by the simulation engine to invoke data generation
 * for each patient. 
 * 
 * @author Melanie Böhmer 
 */

public interface PatientDataGenerator {

     /**
     * This method produces a simulated data value for the given patient and
     * delegates its handling to the provided OutputStrategy.
     *
     * @param patientId the unique identifier of the patient
     * @param outputStrategy the output mechanism used to handle the generated data
     *                   
     * @return This method does not return a value. 
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
