package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

class DataStorageTest {

    private DataStorage storage;

    @BeforeEach
    void setUp() {
        /*
         * DataStorage is a Singleton, so data from one test could remain
         * in the next test. clear() makes every test independent.
         */
        storage = DataStorage.getInstance();
        storage.clear();
    }

    @Test
    void addPatientDataShouldStoreNewRecords() {
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records =
                storage.getRecords(1, 1714376789050L, 1714376789052L);

        assertEquals(2, records.size());
        assertEquals(100.0, records.get(0).getMeasurementValue());
        assertEquals(200.0, records.get(1).getMeasurementValue());
    }

    @Test
    void getRecordsShouldReturnEmptyListForUnknownPatient() {
        List<PatientRecord> records =
                storage.getRecords(999, 1714376789050L, 1714376789052L);

        assertTrue(records.isEmpty());
    }

    @Test
    void getRecordsShouldOnlyReturnRecordsInsideTimeRange() {
        storage.addPatientData(1, 100.0, "HeartRate", 1000L);
        storage.addPatientData(1, 110.0, "HeartRate", 2000L);
        storage.addPatientData(1, 120.0, "HeartRate", 3000L);

        List<PatientRecord> records =
                storage.getRecords(1, 1500L, 2500L);

        assertEquals(1, records.size());
        assertEquals(110.0, records.get(0).getMeasurementValue());
    }

    @Test
    void addPatientDataShouldCreateOnlyOnePatientForSameId() {
        storage.addPatientData(1, 80.0, "HeartRate", 1000L);
        storage.addPatientData(1, 85.0, "HeartRate", 2000L);

        assertEquals(1, storage.getAllPatients().size());
    }

    @Test
    void clearShouldRemoveAllStoredData() {
        storage.addPatientData(1, 80.0, "HeartRate", 1000L);

        storage.clear();

        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void concurrentUpdatesShouldNotLoseRecords() throws InterruptedException {
        int threadCount = 10;
        int recordsPerThread = 100;

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            int threadIndex = i;

            threads[i] = new Thread(() -> {
                for (int j = 0; j < recordsPerThread; j++) {
                    storage.addPatientData(
                            1,
                            j,
                            "HeartRate",
                            threadIndex * recordsPerThread + j
                    );
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        List<PatientRecord> records =
                storage.getRecords(1, 0L, threadCount * recordsPerThread);

        assertEquals(threadCount * recordsPerThread, records.size());
    }

    @Test
    void getInstanceShouldReturnSameObject() {
        DataStorage firstInstance = DataStorage.getInstance();
        DataStorage secondInstance = DataStorage.getInstance();

        assertSame(firstInstance, secondInstance);
    }
}