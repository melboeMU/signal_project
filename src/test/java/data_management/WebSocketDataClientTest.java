package data_management;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.WebSocketDataClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketDataClientTest {

    private DataStorage storage;
    private WebSocketDataClient client;

    @BeforeEach
    public void setUp() throws Exception {
        storage = DataStorage.getInstance();
        storage.clear();

        client = new WebSocketDataClient(new URI("ws://localhost:8887"), storage);
    }

    @Test
    public void testValidMessageIsStored() throws Exception {
        Method method = WebSocketDataClient.class
                .getDeclaredMethod("parseAndStoreMessage", String.class);
        method.setAccessible(true);

        method.invoke(client, "1,1714900000000,HeartRate,82.5");

        List<PatientRecord> records =
                storage.getRecords(1, 1700000000000L, 1800000000000L);

        assertEquals(1, records.size());
        assertEquals("HeartRate", records.get(0).getRecordType());
        assertEquals(82.5, records.get(0).getMeasurementValue());
    }

    @Test
    public void testInvalidMessageFormatThrowsError() throws Exception {
        Method method = WebSocketDataClient.class
                .getDeclaredMethod("parseAndStoreMessage", String.class);
        method.setAccessible(true);

        assertThrows(Exception.class, () -> {
            method.invoke(client, "invalid-message");
        });
    }

    @Test
    public void testInvalidMeasurementValueThrowsError() throws Exception {
        Method method = WebSocketDataClient.class
                .getDeclaredMethod("parseAndStoreMessage", String.class);
        method.setAccessible(true);

        assertThrows(Exception.class, () -> {
            method.invoke(client, "1,1714900000000,HeartRate,notANumber");
        });
    }
    
}