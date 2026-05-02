package alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alerts.Alert;
import com.alerts.types.BloodOxygenAlert;
import com.alerts.types.BloodPressureAlert;
import com.alerts.types.ECGAlert;
import com.alerts.strategy.AlertStrategy;
import com.alerts.strategy.BloodPressureStrategy; 
import com.alerts.strategy.OxygenSaturationStrategy;
import com.alerts.strategy.HeartRateStrategy;      
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests all alert strategies and verifies that they implement the AlertStrategy interface.
 */
class AlertStrategyTest {

    /**
     * Tests that all concrete strategy classes implement AlertStrategy.
     */
    @Test
    void strategiesImplementAlertStrategyInterface() {
        assertInstanceOf(AlertStrategy.class, new BloodPressureStrategy());
        assertInstanceOf(AlertStrategy.class, new OxygenSaturationStrategy());
        assertInstanceOf(AlertStrategy.class, new HeartRateStrategy());
    }

    /**
     * Tests that BloodPressureStrategy creates an alert for critical systolic pressure.
     */
    @Test
    void bloodPressureStrategyCreatesAlertForCriticalSystolicPressure() {
        AlertStrategy strategy = new BloodPressureStrategy();

        List<PatientRecord> records = List.of(
                new PatientRecord(1, 181, "SystolicBloodPressure", 1000L)
        );

        List<Alert> alerts = strategy.checkAlert(records);

        assertEquals(1, alerts.size());
        assertInstanceOf(BloodPressureAlert.class, alerts.get(0));
        assertEquals("1", alerts.get(0).getPatientId());
        assertEquals("Critical blood pressure threshold", alerts.get(0).getCondition());
        assertEquals(1000L, alerts.get(0).getTimestamp());
    }

    /**
     * Tests that BloodPressureStrategy creates an alert for critical diastolic pressure.
     */
    @Test
    void bloodPressureStrategyCreatesAlertForCriticalDiastolicPressure() {
        AlertStrategy strategy = new BloodPressureStrategy();

        List<PatientRecord> records = List.of(
                new PatientRecord(1, 121, "DiastolicBloodPressure", 1000L)
        );

        List<Alert> alerts = strategy.checkAlert(records);

        assertEquals(1, alerts.size());
        assertInstanceOf(BloodPressureAlert.class, alerts.get(0));
        assertEquals("Critical blood pressure threshold", alerts.get(0).getCondition());
    }

    /**
     * Tests that BloodPressureStrategy creates an alert for increasing systolic trend.
     */
    @Test
    void bloodPressureStrategyCreatesAlertForIncreasingSystolicTrend() {
        AlertStrategy strategy = new BloodPressureStrategy();

        List<PatientRecord> records = List.of(
                new PatientRecord(1, 100, "SystolicBloodPressure", 1000L),
                new PatientRecord(1, 112, "SystolicBloodPressure", 2000L),
                new PatientRecord(1, 124, "SystolicBloodPressure", 3000L)
        );

        List<Alert> alerts = strategy.checkAlert(records);

        assertEquals(1, alerts.size());
        assertInstanceOf(BloodPressureAlert.class, alerts.get(0));
        assertEquals("Increasing systolic blood pressure trend", alerts.get(0).getCondition());
        assertEquals(3000L, alerts.get(0).getTimestamp());
    }

    /**
     * Tests that BloodPressureStrategy creates an alert for decreasing diastolic trend.
     */
    @Test
    void bloodPressureStrategyCreatesAlertForDecreasingDiastolicTrend() {
        AlertStrategy strategy = new BloodPressureStrategy();

        List<PatientRecord> records = List.of(
                new PatientRecord(1, 100, "DiastolicBloodPressure", 1000L),
                new PatientRecord(1, 88, "DiastolicBloodPressure", 2000L),
                new PatientRecord(1, 76, "DiastolicBloodPressure", 3000L)
        );

        List<Alert> alerts = strategy.checkAlert(records);

        assertEquals(1, alerts.size());
        assertInstanceOf(BloodPressureAlert.class, alerts.get(0));
        assertEquals("Decreasing diastolic blood pressure trend", alerts.get(0).getCondition());
        assertEquals(3000L, alerts.get(0).getTimestamp());
    }

    /**
     * Tests that OxygenSaturationStrategy creates an alert for low oxygen saturation.
     */
    @Test
    void oxygenSaturationStrategyCreatesAlertForLowOxygenSaturation() {
        AlertStrategy strategy = new OxygenSaturationStrategy();

        List<PatientRecord> records = List.of(
                new PatientRecord(2, 91, "BloodSaturation", 1000L)
        );

        List<Alert> alerts = strategy.checkAlert(records);

        assertEquals(1, alerts.size());
        assertInstanceOf(BloodOxygenAlert.class, alerts.get(0));
        assertEquals("2", alerts.get(0).getPatientId());
        assertEquals("Low blood oxygen saturation", alerts.get(0).getCondition());
        assertEquals(1000L, alerts.get(0).getTimestamp());
    }

    /**
     * Tests that OxygenSaturationStrategy creates an alert for a rapid oxygen saturation drop.
     */
    @Test
    void oxygenSaturationStrategyCreatesAlertForRapidOxygenDrop() {
        AlertStrategy strategy = new OxygenSaturationStrategy();

        List<PatientRecord> records = List.of(
                new PatientRecord(2, 98, "BloodSaturation", 1000L),
                new PatientRecord(2, 93, "BloodSaturation", 2000L)
        );

        List<Alert> alerts = strategy.checkAlert(records);

        assertEquals(1, alerts.size());
        assertInstanceOf(BloodOxygenAlert.class, alerts.get(0));
        assertEquals("Rapid blood oxygen saturation drop", alerts.get(0).getCondition());
        assertEquals(2000L, alerts.get(0).getTimestamp());
    }

    /**
     * Tests that HeartRateStrategy creates an alert for an abnormal ECG peak.
     */
    @Test
    void heartRateStrategyCreatesAlertForAbnormalEcgPeak() {
        AlertStrategy strategy = new HeartRateStrategy();

        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(3, 10, "ECG", 1000L));
        records.add(new PatientRecord(3, 10, "ECG", 2000L));
        records.add(new PatientRecord(3, 10, "ECG", 3000L));
        records.add(new PatientRecord(3, 10, "ECG", 4000L));
        records.add(new PatientRecord(3, 10, "ECG", 5000L));
        records.add(new PatientRecord(3, 25, "ECG", 6000L));

        List<Alert> alerts = strategy.checkAlert(records);

        assertEquals(1, alerts.size());
        assertInstanceOf(ECGAlert.class, alerts.get(0));
        assertEquals("3", alerts.get(0).getPatientId());
        assertEquals("Abnormal ECG peak", alerts.get(0).getCondition());
        assertEquals(6000L, alerts.get(0).getTimestamp());
    }

    /**
     * Tests that strategies return no alerts when no condition is met.
     */
    @Test
    void strategiesReturnNoAlertsWhenNoConditionIsMet() {
        List<PatientRecord> records = List.of(
                new PatientRecord(1, 120, "SystolicBloodPressure", 1000L),
                new PatientRecord(1, 80, "DiastolicBloodPressure", 2000L),
                new PatientRecord(1, 98, "BloodSaturation", 3000L),
                new PatientRecord(1, 10, "ECG", 4000L),
                new PatientRecord(1, 10, "ECG", 5000L),
                new PatientRecord(1, 10, "ECG", 6000L),
                new PatientRecord(1, 10, "ECG", 7000L),
                new PatientRecord(1, 10, "ECG", 8000L),
                new PatientRecord(1, 15, "ECG", 9000L)
        );

        assertTrue(new BloodPressureStrategy().checkAlert(records).isEmpty());
        assertTrue(new OxygenSaturationStrategy().checkAlert(records).isEmpty());
        assertTrue(new HeartRateStrategy().checkAlert(records).isEmpty());
    }
}