package data_management;
import com.data_management.DataStorage;
import com.data_management.PatientRecord;   
import com.data_management.WebSocketDataClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketErrorHandlingTest {

    private DataStorage storage;
    private WebSocketDataClient client;

    @BeforeEach
    void setUp() throws Exception {
        storage = DataStorage.getInstance();
        storage.clear();

        client = new WebSocketDataClient(
                new URI("ws://localhost:8887"),
                storage
        );
    }

    @Test
    void corruptedMessageShouldNotCrashSystem() {
        assertDoesNotThrow(() -> {
            client.onMessage("corrupted-message");
        });

        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void invalidNumericDataShouldNotBeStored() {
        client.onMessage("1,1714900000000,HeartRate,notANumber");

        List<PatientRecord> records =
                storage.getRecords(1, 1700000000000L, 1800000000000L);

        assertTrue(records.isEmpty());
    }

    @Test
    void systemShouldContinueAfterCorruptedMessage() {
        client.onMessage("bad-message");
        client.onMessage("1,1714900000000,HeartRate,82.0");

        List<PatientRecord> records =
                storage.getRecords(1, 1700000000000L, 1800000000000L);

        assertEquals(1, records.size());
        assertEquals(82.0, records.get(0).getMeasurementValue());
    }

    @Test
    void connectionLossShouldNotDeleteExistingData() {
        client.onMessage("1,1714900000000,HeartRate,82.0");

        assertDoesNotThrow(() -> {
            client.onClose(1006, "Connection lost", true);
        });

        List<PatientRecord> records =
                storage.getRecords(1, 1700000000000L, 1800000000000L);

        assertEquals(1, records.size());
    }

    @Test
    void networkErrorShouldNotDeleteExistingData() {
        client.onMessage("1,1714900000000,HeartRate,82.0");

        assertDoesNotThrow(() -> {
            client.onError(new RuntimeException("Simulated network failure"));
        });

        List<PatientRecord> records =
                storage.getRecords(1, 1700000000000L, 1800000000000L);

        assertEquals(1, records.size());
    }

    @Test
    void multipleTransmissionFailuresShouldNotCrashSystem() {
        assertDoesNotThrow(() -> {
            client.onMessage("");
            client.onMessage("1,badTimestamp,HeartRate,82.0");
            client.onMessage("abc,1714900000000,HeartRate,82.0");
            client.onError(new RuntimeException("Network failure"));
            client.onClose(1006, "Unexpected disconnect", true);
        });

        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void validDataAfterNetworkErrorShouldStillBeProcessed() {
        client.onError(new RuntimeException("Temporary network error"));

        client.onMessage("1,1714900000000,HeartRate,82.0");

        List<PatientRecord> records =
                storage.getRecords(1, 1700000000000L, 1800000000000L);

        assertEquals(1, records.size());
        assertEquals("HeartRate", records.get(0).getRecordType());
    }
}