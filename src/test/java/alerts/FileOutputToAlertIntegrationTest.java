package alerts;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.alerts.AlertGenerator;
import com.cardioGenerator.outputs.FileOutputStrategy;
import com.data_management.DataReader;
import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.RecordLabels;

/**
 * Integration test for the full generated-file-to-alert flow.
 *
 * Flow tested:
 *
 * FileOutputStrategy
 *      -> generated-style output file
 *      -> FileDataReader
 *      -> DataStorage
 *      -> AlertGenerator
 */
class FileOutputToAlertIntegrationTest {

    @TempDir
    Path tempOutputDirectory;

    private DataStorage storage;

    @BeforeEach
    void setUp() {
        storage = DataStorage.getInstance();

        // IMPORTANT:
        // DataStorage is a Singleton, so previous tests may leave data behind.
        storage.clear();
    }

    @Test
    void generatedFileOutputCanBeReadIntoStorageAndEvaluatedForAlerts() throws IOException {
        // ARRANGE
        // CHANGED/NEW:
        // Use the real FileOutputStrategy to create generated-style output files.
        FileOutputStrategy outputStrategy =
                new FileOutputStrategy(tempOutputDirectory.toString());

        int patientId = 1;
        long timestamp = 1713772800000L;

        // CHANGED/NEW:
        // These records are written using the same path as the simulator output.
        outputStrategy.output(patientId, timestamp, RecordLabels.HEART_RATE, "78.0");
        outputStrategy.output(patientId, timestamp + 1000, RecordLabels.BLOOD_SATURATION, "98.0");

        // CHANGED/NEW:
        // Use the real FileDataReader to read the generated files.
        DataReader reader = new FileDataReader(tempOutputDirectory);

        // ACT
        reader.readData(storage);

        // ASSERT: data reached DataStorage
        List<Patient> patients = storage.getAllPatients();

        assertEquals(1, patients.size());

        List<PatientRecord> records = storage.getRecords(
                patientId,
                Long.MIN_VALUE,
                Long.MAX_VALUE
        );

        assertEquals(2, records.size());

        assertFalse(records.isEmpty());

        // ASSERT: alert evaluation runs on the loaded patient data
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        Patient loadedPatient = patients.get(0);

        assertDoesNotThrow(() -> alertGenerator.evaluateData(loadedPatient));
    }
}
