package alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.alerts.Alert;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.BloodOxygenAlertFactory;  
import com.alerts.factory.BloodPressureAlertFactory;
import com.alerts.factory.ECGAlertFactory;      
import com.alerts.types.BloodOxygenAlert;
import com.alerts.types.BloodPressureAlert;
import com.alerts.types.ECGAlert;
import org.junit.jupiter.api.Test;

/**
 * Tests the alert factory classes and the concrete alert types.
 */
class AlertFactoryTest {

    /**
     * Tests that BloodPressureAlertFactory creates a BloodPressureAlert.
     */
    @Test
    void createAlertCreatesBloodPressureAlert() {
        AlertFactory factory = new BloodPressureAlertFactory();

        Alert alert = factory.createAlert("1", "Critical blood pressure", 1000L);

        assertInstanceOf(BloodPressureAlert.class, alert);
        assertEquals("1", alert.getPatientId());
        assertEquals("Critical blood pressure", alert.getCondition());
        assertEquals(1000L, alert.getTimestamp());
    }

    /**
     * Tests that BloodOxygenAlertFactory creates a BloodOxygenAlert.
     */
    @Test
    void createAlertCreatesBloodOxygenAlert() {
        AlertFactory factory = new BloodOxygenAlertFactory();

        Alert alert = factory.createAlert("2", "Low blood oxygen saturation", 2000L);

        assertInstanceOf(BloodOxygenAlert.class, alert);
        assertEquals("2", alert.getPatientId());
        assertEquals("Low blood oxygen saturation", alert.getCondition());
        assertEquals(2000L, alert.getTimestamp());
    }

    /**
     * Tests that ECGAlertFactory creates an ECGAlert.
     */
    @Test
    void createAlertCreatesEcgAlert() {
        AlertFactory factory = new ECGAlertFactory();

        Alert alert = factory.createAlert("3", "Abnormal ECG peak", 3000L);

        assertInstanceOf(ECGAlert.class, alert);
        assertEquals("3", alert.getPatientId());
        assertEquals("Abnormal ECG peak", alert.getCondition());
        assertEquals(3000L, alert.getTimestamp());
    }

    /**
     * Tests that BloodPressureAlert stores the given alert data correctly.
     */
    @Test
    void bloodPressureAlertStoresDataCorrectly() {
        BloodPressureAlert alert = new BloodPressureAlert("4", "High systolic pressure", 4000L);

        assertEquals("4", alert.getPatientId());
        assertEquals("High systolic pressure", alert.getCondition());
        assertEquals(4000L, alert.getTimestamp());
    }

    /**
     * Tests that BloodOxygenAlert stores the given alert data correctly.
     */
    @Test
    void bloodOxygenAlertStoresDataCorrectly() {
        BloodOxygenAlert alert = new BloodOxygenAlert("5", "Rapid oxygen drop", 5000L);

        assertEquals("5", alert.getPatientId());
        assertEquals("Rapid oxygen drop", alert.getCondition());
        assertEquals(5000L, alert.getTimestamp());
    }

    /**
     * Tests that ECGAlert stores the given alert data correctly.
     */
    @Test
    void ecgAlertStoresDataCorrectly() {
        ECGAlert alert = new ECGAlert("6", "Irregular heart rhythm", 6000L);

        assertEquals("6", alert.getPatientId());
        assertEquals("Irregular heart rhythm", alert.getCondition());
        assertEquals(6000L, alert.getTimestamp());
    }
}
