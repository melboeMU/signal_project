package com.cardioGenerator.outputs;//package name changed to lowerCamelCase

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class implements the OutputStrategy interface to write generated patient data to files on the local file system.
 * persist patient data into text files.
 *
 * This class is used as part of the data simulation pipeline to store generated patient
 * data persistently.
 * 
 * @author Melanie Böhmer 
 */

public class FileOutputStrategy implements OutputStrategy {

    private String baseDirectory;//name chaneged to lowerCamelCase for variable declaration

    // Changed variable name to camelCase instead of underscore (no real constant so no all uppercase needed)
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Constructs a new FileOutputStrategy with a specified base directory.
     *
     * @param baseDirectory the directory where output files will be stored;
     * @throws NullPointerException if {@code baseDirectory} is {@code null}
     
     */
    public FileOutputStrategy(String baseDirectory) {
        //change to lowerCamelCase style as in line 12 
        this.baseDirectory = baseDirectory;
    }

    /**
     * Writes a single patient data record to a file corresponding to the given label.
     * The output format is: Patient ID: <id>, Timestamp: <timestamp>, Label: <label>, Data: <data>}
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the time of the measurement in milliseconds 
     * @param label the category of the data (e.g., "HeartRate", "Saturation");also used as the file name
     * @param data the measurement value as a string
     *
     * @return This method does not return a value. The data is written directly to a file.
     *
     * @throws NullPointerException if {@code label} or {@code data} is {@code null}
     */

    @Override 
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            // Changed BaseDirectory to baseDirectory to follow corrected field naming
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        // Changed variable name Filepath and file_map and BaseDirectory to lowerCamelCase
        //added  breaks at a higher syntactic level
        String filePath = fileMap.computeIfAbsent(
                                label,
                                k -> Paths.get(baseDirectory, label + ".txt").toString()
                          );

        // Write the data to the file
        // added breaks at commas and higher syntactic level to make it more readable                    
        try (PrintWriter out = 
                new PrintWriter(
                        Files.newBufferedWriter(
                            Paths.get(filePath),//change name to lowerCamelCase
                            StandardOpenOption.CREATE, 
                            StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());//change Filepath to lowerCamelCase
        }
    }
}