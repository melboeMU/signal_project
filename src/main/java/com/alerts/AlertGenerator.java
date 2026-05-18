package com.alerts;

import com.alerts.factory.AlertFactory;
import com.alerts.factory.BloodOxygenAlertFactory;
import com.alerts.factory.ECGAlertFactory;
import com.alerts.strategy.AlertStrategy;
import com.alerts.strategy.BloodPressureStrategy;
import com.alerts.strategy.HeartRateStrategy;
import com.alerts.strategy.OxygenSaturationStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.RecordLabels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The AlertGenerator class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met.
 */
public class AlertGenerator {
    private final DataStorage dataStorage;

    /*
     * Stores generated alerts so they can be verified in unit tests.
     */
    private final List<Alert> generatedAlerts = new ArrayList<>();

    /*
     * Strategies are responsible for checking specific alert conditions.
     * Each strategy creates its alerts through the matching AlertFactory.
     */
    private final List<AlertStrategy> alertStrategies;

    /*
     * Factories used for alert conditions that are still evaluated directly
     * inside AlertGenerator because no separate strategy class exists for them yet.
     */
    private final AlertFactory bloodOxygenAlertFactory = new BloodOxygenAlertFactory();
    private final AlertFactory ecgAlertFactory = new ECGAlertFactory();

    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alertStrategies = List.of(
                new BloodPressureStrategy(),
                new OxygenSaturationStrategy(),
                new HeartRateStrategy()
        );
    }

    /**
     * Constructor that allows tests or future code to inject custom strategies.
     *
     * @param dataStorage the data storage used to retrieve patient records
     * @param alertStrategies the strategies used to evaluate patient records
     */
    public AlertGenerator(DataStorage dataStorage, List<AlertStrategy> alertStrategies) {
        this.dataStorage = dataStorage;
        this.alertStrategies = new ArrayList<>(alertStrategies);
    }

    /**
     * Evaluates all available patient records and triggers alerts when predefined
     * medical conditions are detected.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        if (patient == null) {
            return;
        }

        List<PatientRecord> records = dataStorage.getRecords(
                patient.getPatientId(),
                0,
                Long.MAX_VALUE
        );

        if (records == null || records.isEmpty()) {
            return;
        }

        records.sort(Comparator.comparingLong(PatientRecord::getTimestamp));

        for (AlertStrategy strategy : alertStrategies) {
            for (Alert alert : strategy.checkAlert(records)) {
                triggerAlert(alert);
            }
        }

        evaluateHypotensiveHypoxemiaAlert(records);
        evaluateTriggeredAlerts(records);
    }

    private void evaluateHypotensiveHypoxemiaAlert(List<PatientRecord> records) {
        List<PatientRecord> systolicRecords = getRecordsByType(records, RecordLabels.SYSTOLIC_BLOOD_PRESSURE);
        List<PatientRecord> saturationRecords = getRecordsByType(records, RecordLabels.BLOOD_SATURATION);

        for (PatientRecord systolicRecord : systolicRecords) {
            if (systolicRecord.getMeasurementValue() >= 90) {
                continue;
            }

            for (PatientRecord saturationRecord : saturationRecords) {
                if (saturationRecord.getMeasurementValue() < 92) {
                    triggerAlert(bloodOxygenAlertFactory.createAlert(
                            String.valueOf(systolicRecord.getPatientId()),
                            "Hypotensive hypoxemia alert",
                            Math.max(
                                    systolicRecord.getTimestamp(),
                                    saturationRecord.getTimestamp()
                            )
                    ));
                    return;
                }
            }
        }
    }

    private void evaluateTriggeredAlerts(List<PatientRecord> records) {
        List<PatientRecord> triggeredRecords = getRecordsByType(records, RecordLabels.TRIGGERED_ALERT);

        for (PatientRecord record : triggeredRecords) {
            /*
             * Assumption:
             * 1 means the alert button was pressed.
             * 0 means the alert button was not pressed.
             */
            if (record.getMeasurementValue() == 1) {
                triggerAlert(ecgAlertFactory.createAlert(
                        String.valueOf(record.getPatientId()),
                        "Manual triggered alert",
                        record.getTimestamp()
                ));
            }
        }
    }

    private List<PatientRecord> getRecordsByType(List<PatientRecord> records, String recordType) {
        return records.stream()
                .filter(record -> RecordLabels.normalize(record.getRecordType()).equals(recordType))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .collect(Collectors.toList());
    }

    private void triggerAlert(Alert alert) {
        generatedAlerts.add(alert);
    }

    /**
     * Returns all generated alerts.
     * This method is mainly useful for testing.
     *
     * @return list of generated alerts
     */
    public List<Alert> getGeneratedAlerts() {
        return generatedAlerts;
    }
}
