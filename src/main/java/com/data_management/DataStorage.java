package com.data_management;

import java.util.ArrayList;
import java.io.IOException; 
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import com.alerts.AlertGenerator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages storage and retrieval of patient data within a healthcare monitoring
 * system.
 * This class serves as a repository for all patient records, organized by
 * patient IDs.
 */
public class DataStorage {
    private static DataStorage instance;

    private final Map<Integer, Patient> patientMap;

    /**
     * Constructs a DataStorage instance.(singelton pattern)
     * Private to prevent direct object creation from outside the class.
     */
    private DataStorage() {
        this.patientMap = new ConcurrentHashMap<>();
    }

    /**
     * Returns the single DataStorage instance.
     *
     * @return the singleton DataStorage instance
     */
    public static DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    /**
     * Adds or updates patient data in the storage.
     * If the patient does not exist, a new Patient object is created and added to
     * the storage.
     * Otherwise, the new data is added to the existing patient's reco rds.
     *
     * @param patientId        the unique identifier of the patient
     * @param measurementValue the value of the health metric being recorded
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since the Unix epoch
     */
    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {

        // computeIfAbsent prevents duplicate Patient objects from being created
        // when multiple WebSocket messages arrive at nearly the same time.
        Patient patient = patientMap.computeIfAbsent(patientId, Patient::new);

        // Synchronizing on the patient protects the patient's internal record list
        // from concurrent updates.
        synchronized (patient) {
            patient.addRecord(measurementValue, recordType, timestamp);
        }
    }

    /**
     * Retrieves a list of PatientRecord objects for a specific patient, filtered by
     * a time range.
     *
     * @param patientId the unique identifier of the patient whose records are to be
     *                  retrieved
     * @param startTime the start of the time range, in milliseconds since the Unix
     *                  epoch
     * @param endTime   the end of the time range, in milliseconds since the Unix
     *                  epoch
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        if (patient != null) {
            // Synchronization prevents reading records while another thread is adding one.
            synchronized (patient) {
                return patient.getRecords(startTime, endTime);
            }
        }
        return new ArrayList<>(); // return an empty list if no patient is found
    }

    /**
     * Retrieves a collection of all patients stored in the data storage.
     *
     * @return a list of all patients
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * The main method for the DataStorage class.
     * This method now accepts an input directory, reads patient records from files,
     * stores them in DataStorage, and evaluates alerts.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {

        // validate command line argument
        if (args.length != 1) {
            System.err.println("Usage: java com.data_management.DataStorage <input-directory>");
            System.err.println("Example: java com.data_management.DataStorage output");
            return;
        }
        // read input directory from command line
        Path inputDirectory = Path.of(args[0]);

        DataStorage storage = DataStorage.getInstance();

         // create FileDataReader using the input directory
        DataReader reader = new FileDataReader(inputDirectory);

         try {
            // CHANGED: actually read file data into DataStorage
            reader.readData(storage);
            System.out.println("Successfully loaded patient data from: " + inputDirectory);
        } catch (IOException e) {
            // CHANGED: stop program if file reading fails
            System.err.println("Failed to read patient data: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        // print all loaded records instead of only hardcoding patient 1
        for (Patient patient : storage.getAllPatients()) {
            List<PatientRecord> records = storage.getRecords(
                    patient.getPatientId(),
                    Long.MIN_VALUE,
                    Long.MAX_VALUE
            );

            for (PatientRecord record : records) {
                System.out.println("Record for Patient ID: " + record.getPatientId()
                        + ", Type: " + record.getRecordType()
                        + ", Data: " + record.getMeasurementValue()
                        + ", Timestamp: " + record.getTimestamp());
            }
        }

        // Initialize the AlertGenerator with the storage
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        // Evaluate all patients' data to check for conditions that may trigger alerts
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }
    }
    /**
     * Clears all stored patient data.
     * 
     * This method is mainly used for testing because DataStorage
     * is implemented as a Singleton and persists data between tests.
     */
    public void clear() {
        patientMap.clear();
    }
}
