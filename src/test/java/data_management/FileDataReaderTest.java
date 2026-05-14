package data_management;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.FileDataReader;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.data_management.RecordLabels;

class FileDataReaderTest {

    private DataStorage storage;

    @BeforeEach
    void setUp() {
        storage = DataStorage.getInstance();
        storage.clear();
    }

    @Test
    void shouldReadSingleFileAndStoreAllMeasurements() throws IOException {
        Path tempDir = Files.createTempDirectory("reader-test");
        Path file = tempDir.resolve("output1.csv");
        Files.writeString(file,
                "timestamp,patientId,recordType,measurementValue\n" +
                "1713772800000,1," + RecordLabels.HEART_RATE + ",78.0\n" +
                "1713772860000,1," + RecordLabels.SYSTOLIC_BLOOD_PRESSURE + ",120.0\n");

        FileDataReader reader = new FileDataReader(tempDir);

        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 1713770000000L, 1713779999999L);

        assertEquals(2, records.size());
        assertEquals(1, records.get(0).getPatientId());
        assertEquals(RecordLabels.HEART_RATE, records.get(0).getRecordType());
        assertEquals(78.0, records.get(0).getMeasurementValue());
        assertEquals(1713772800000L, records.get(0).getTimestamp());
    }

    @Test
    void shouldReadMultipleFilesInDirectory() throws IOException {
        Path tempDir = Files.createTempDirectory("reader-test");
        Files.writeString(tempDir.resolve("a.csv"),
                "timestamp,patientId,recordType,measurementValue\n" +
                "1713772800000,1," + RecordLabels.HEART_RATE + ",80.0\n");

        Files.writeString(tempDir.resolve("b.csv"),
                "timestamp,patientId,recordType,measurementValue\n" +
                "1713772860000,2," + RecordLabels.HEART_RATE + ",90.0\n");

        FileDataReader reader = new FileDataReader(tempDir); 

        reader.readData(storage);

        List<PatientRecord> patient1Records = storage.getRecords(1, 1713770000000L, 1713779999999L);
        List<PatientRecord> patient2Records = storage.getRecords(2, 1713770000000L, 1713779999999L);

        assertEquals(1, patient1Records.size());
        assertEquals(1, patient2Records.size());
    }

    @Test
    void shouldIgnoreHeaderAndEmptyLines() throws IOException {
        Path tempDir = Files.createTempDirectory("reader-test");
        Files.writeString(tempDir.resolve("output.csv"),
                "timestamp,patientId,recordType,measurementValue\n" +
                "\n" +
                "1713772800000,1," + RecordLabels.HEART_RATE + ",78.0\n" +
                "\n");

        FileDataReader reader = new FileDataReader(tempDir);

        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 1713770000000L, 1713779999999L);

        assertEquals(1, records.size());
    }

    @Test
    void shouldThrowIOExceptionWhenDirectoryDoesNotExist() {
        Path tempDir;
        try {
            tempDir = Files.createTempDirectory("reader-test");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Path missingDir = tempDir.resolve("does_not_exist");
        FileDataReader reader = new FileDataReader(missingDir);

        assertThrows(IOException.class, () -> reader.readData(storage));
    }

    @Test
    void shouldThrowIOExceptionForMalformedLine() throws IOException {
        Path tempDir = Files.createTempDirectory("reader-test");
        Files.writeString(tempDir.resolve("broken.csv"),
                "timestamp,patientId,recordType,measurementValue\n" +
                "1713772800000,1," + RecordLabels.HEART_RATE + "\n");

        FileDataReader reader = new FileDataReader(tempDir);

        assertThrows(IOException.class, () -> reader.readData(storage));
    }

    @Test
    void shouldSupportIsoTimestampFormat() throws IOException {
        Path tempDir = Files.createTempDirectory("reader-test");
        Files.writeString(tempDir.resolve("iso.csv"),
                "timestamp,patientId,recordType,measurementValue\n" +
                "2026-04-22T10:15:30,1," + RecordLabels.HEART_RATE + ",77.0\n");

        FileDataReader reader = new FileDataReader(tempDir);

        reader.readData(storage);

        List<PatientRecord> allRecords = storage.getRecords(1, 0L, Long.MAX_VALUE);

        assertEquals(1, allRecords.size());
        assertEquals(RecordLabels.HEART_RATE, allRecords.get(0).getRecordType());
        assertEquals(77.0, allRecords.get(0).getMeasurementValue());
    }
}
