package data_management;

import com.alerts.AlertGenerator;

import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertGeneratorTest {

    private AlertGenerator alertGenerator;
    private Patient patient;
    private DataStorage dataStorage;

    @BeforeEach
    void setUp() {
        dataStorage = new DataStorage();
        alertGenerator = new AlertGenerator(dataStorage);
        patient = new Patient(1);
    }
    private void addRecord(double value, String recordType, long timestamp) {
        dataStorage.addPatientData(
                patient.getPatientId(),
                value,
                recordType,
                timestamp
        );
    }

    // BLOOD PRESSURE TESTS

    @Test
    void shouldTriggerIncreasingSystolicTrendAlert() {
        addRecord(120, "SystolicBloodPressure", 1000);
        addRecord(135, "SystolicBloodPressure", 2000);
        addRecord(150, "SystolicBloodPressure", 3000);

        alertGenerator.evaluateData(patient);

        assertTrue(alertGenerator.getGeneratedAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Increasing systolic")));
    }

    @Test
    void shouldTriggerDecreasingDiastolicTrendAlert() {
        addRecord(100, "DiastolicBloodPressure", 1000);
        addRecord(85, "DiastolicBloodPressure", 2000);
        addRecord(70, "DiastolicBloodPressure", 3000);

        alertGenerator.evaluateData(patient);

        assertTrue(alertGenerator.getGeneratedAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Decreasing diastolic")));
    }

    @Test
    void shouldTriggerCriticalHighBloodPressureAlert() {
        addRecord(181, "SystolicBloodPressure", 1000);

        alertGenerator.evaluateData(patient);

        assertTrue(alertGenerator.getGeneratedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Critical blood pressure threshold")));
    }

    @Test
    void shouldTriggerCriticalLowBloodPressureAlert() {
        addRecord(85, "SystolicBloodPressure", 1000);

        alertGenerator.evaluateData(patient);

        assertTrue(alertGenerator.getGeneratedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Critical blood pressure threshold")));
    }

    // BLOOD SATURATION TESTS

    @Test
    void shouldTriggerLowSaturationAlert() {
        addRecord(91, "BloodSaturation", 1000);

        alertGenerator.evaluateData(patient);

        assertTrue(alertGenerator.getGeneratedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Low blood oxygen saturation")));
    }

    @Test
    void shouldTriggerRapidDropAlertWithin10Minutes() {
        addRecord(98, "BloodSaturation", 1000);
        addRecord(92, "BloodSaturation", 1000 + 5 * 60 * 1000); // 5 min later

        alertGenerator.evaluateData(patient);

        assertTrue(alertGenerator.getGeneratedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Rapid blood oxygen saturation drop")));
    }

    @Test
    void shouldNotTriggerRapidDropIfOutsideTimeWindow() {
        addRecord(98, "BloodSaturation", 1000);
        addRecord(92, "BloodSaturation", 1000 + 15 * 60 * 1000); // 15 min later

        alertGenerator.evaluateData(patient);

        assertFalse(alertGenerator.getGeneratedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Rapid blood oxygen saturation drop")));
    }

    // COMBINED ALERT TEST

    @Test
    void shouldTriggerHypotensiveHypoxemiaAlert() {
        addRecord(85, "SystolicBloodPressure", 1000);
        addRecord(90, "BloodSaturation", 2000);

        alertGenerator.evaluateData(patient);

        assertTrue(alertGenerator.getGeneratedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Hypotensive hypoxemia alert")));
    }

    @Test
    void shouldNotTriggerCombinedAlertIfOnlyOneConditionMet() {
        addRecord(85, "SystolicBloodPressure", 1000);
        addRecord(95, "BloodSaturation", 2000);

        alertGenerator.evaluateData(patient);

        assertFalse(alertGenerator.getGeneratedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Hypotensive hypoxemia alert")));
    }

    // ECG TESTS

    @Test
    void shouldTriggerAbnormalEcgPeak() {
        // normal values
        addRecord(1, "ECG", 1000);
        addRecord(1, "ECG", 2000);
        addRecord(1, "ECG", 3000);
        addRecord(1, "ECG", 4000);
        addRecord(1, "ECG", 5000);

        // abnormal spike
        addRecord(3, "ECG", 6000);

        alertGenerator.evaluateData(patient);

        assertTrue(alertGenerator.getGeneratedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Abnormal ECG peak")));
    }

    @Test
    void shouldNotTriggerEcgAlertForNormalValues() {
        for (int i = 0; i < 6; i++) {
            addRecord(1, "ECG", 1000 + i * 1000);
        }

        alertGenerator.evaluateData(patient);

        assertFalse(alertGenerator.getGeneratedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Abnormal ECG peak")));
    }

    // MANUAL ALERT TESTS

    @Test
    void shouldTriggerManualAlert() {
        addRecord(1, "TriggeredAlert", 1000);

        alertGenerator.evaluateData(patient);

        assertTrue(alertGenerator.getGeneratedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Manual triggered alert")));
    }

    @Test
    void shouldNotTriggerManualAlertWhenValueIsZero() {
        addRecord(0, "TriggeredAlert", 1000);

        alertGenerator.evaluateData(patient);

        assertTrue(alertGenerator.getGeneratedAlerts().isEmpty());
    }

    // EDGE CASE TESTS

    @Test
    void shouldHandleEmptyPatientData() {
        alertGenerator.evaluateData(patient);

        assertTrue(alertGenerator.getGeneratedAlerts().isEmpty());
    }

    @Test
    void shouldHandleNullPatient() {
        alertGenerator.evaluateData(null);

        assertTrue(alertGenerator.getGeneratedAlerts().isEmpty());
    }
}