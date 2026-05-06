package data_management;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.WebSocketDataClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketAlertIntegrationTest {

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
    void websocketDataShouldBeStoredAndEvaluatedByAlertGenerator() {
        client.onMessage("1,1714900000000,HeartRate,150.0");

        assertEquals(1, storage.getAllPatients().size());

        Patient patient = storage.getAllPatients().get(0);
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        assertDoesNotThrow(() -> {
            alertGenerator.evaluateData(patient);
        });
    }
}