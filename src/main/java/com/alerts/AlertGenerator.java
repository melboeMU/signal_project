package com.alerts;

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
    private DataStorage dataStorage;

    /*
     * Stores generated alerts so they can be verified in unit tests.
     */
    private final List<Alert> generatedAlerts = new ArrayList<>();

    private static final long TEN_MINUTES_IN_MILLIS = 10 * 60 * 1000;

    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
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

        evaluateBloodPressureAlerts(records);
        evaluateBloodSaturationAlerts(records);
        evaluateHypotensiveHypoxemiaAlert(records);
        evaluateEcgAlerts(records);
        evaluateTriggeredAlerts(records);
    }

    private void evaluateBloodPressureAlerts(List<PatientRecord> records) {
        List<PatientRecord> systolicRecords = getRecordsByType(records, RecordLabels.SYSTOLIC_BLOOD_PRESSURE);
        List<PatientRecord> diastolicRecords = getRecordsByType(records, RecordLabels.DIASTOLIC_BLOOD_PRESSURE);

        evaluateBloodPressureThresholds(systolicRecords, true);
        evaluateBloodPressureThresholds(diastolicRecords, false);

        evaluateBloodPressureTrend(systolicRecords, "Increasing systolic blood pressure trend",
                "Decreasing systolic blood pressure trend");

        evaluateBloodPressureTrend(diastolicRecords, "Increasing diastolic blood pressure trend",
                "Decreasing diastolic blood pressure trend");
    }

    private void evaluateBloodPressureThresholds(List<PatientRecord> records, boolean systolic) {
        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();

            boolean thresholdExceeded;

            if (systolic) {
                thresholdExceeded = value > 180 || value < 90;
            } else {
                thresholdExceeded = value > 120 || value < 60;
            }

            if (thresholdExceeded) {
                triggerAlert(new Alert(
                        String.valueOf(record.getPatientId()),
                        "Critical blood pressure threshold",
                        record.getTimestamp()
                ));
            }
        }
    }

    private void evaluateBloodPressureTrend(
            List<PatientRecord> records,
            String increasingCondition,
            String decreasingCondition
    ) {
        if (records.size() < 3) {
            return;
        }

        for (int i = 0; i <= records.size() - 3; i++) {
            PatientRecord first = records.get(i);
            PatientRecord second = records.get(i + 1);
            PatientRecord third = records.get(i + 2);

            double firstChange = second.getMeasurementValue() - first.getMeasurementValue();
            double secondChange = third.getMeasurementValue() - second.getMeasurementValue();

            if (firstChange > 10 && secondChange > 10) {
                triggerAlert(new Alert(
                        String.valueOf(third.getPatientId()),
                        increasingCondition,
                        third.getTimestamp()
                ));
            }

            if (firstChange < -10 && secondChange < -10) {
                triggerAlert(new Alert(
                        String.valueOf(third.getPatientId()),
                        decreasingCondition,
                        third.getTimestamp()
                ));
            }
        }
    }

    private void evaluateBloodSaturationAlerts(List<PatientRecord> records) {
        List<PatientRecord> saturationRecords = getRecordsByType(records, RecordLabels.BLOOD_SATURATION);

        for (PatientRecord record : saturationRecords) {
            if (record.getMeasurementValue() < 92) {
                triggerAlert(new Alert(
                        String.valueOf(record.getPatientId()),
                        "Low blood oxygen saturation",
                        record.getTimestamp()
                ));
            }
        }

        for (int i = 0; i < saturationRecords.size(); i++) {
            PatientRecord earlier = saturationRecords.get(i);

            for (int j = i + 1; j < saturationRecords.size(); j++) {
                PatientRecord later = saturationRecords.get(j);

                if (later.getTimestamp() - earlier.getTimestamp() > TEN_MINUTES_IN_MILLIS) {
                    break;
                }

                if (earlier.getMeasurementValue() - later.getMeasurementValue() >= 5) {
                    triggerAlert(new Alert(
                            String.valueOf(later.getPatientId()),
                            "Rapid blood oxygen saturation drop",
                            later.getTimestamp()
                    ));
                    break;
                }
            }
        }
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
                    triggerAlert(new Alert(
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

    private void evaluateEcgAlerts(List<PatientRecord> records) {
        List<PatientRecord> ecgRecords = getRecordsByType(records, RecordLabels.ECG);

        int slidingWindowSize = 5;

        if (ecgRecords.size() <= slidingWindowSize) {
            return;
        }

        for (int i = slidingWindowSize; i < ecgRecords.size(); i++) {
            double sum = 0;

            for (int j = i - slidingWindowSize; j < i; j++) {
                sum += ecgRecords.get(j).getMeasurementValue();
            }

            double average = sum / slidingWindowSize;
            double currentValue = ecgRecords.get(i).getMeasurementValue();

            /*
             * Assumption:
             * A peak is abnormal when it is more than twice the average
             * of the previous five ECG readings.
             */
            if (currentValue > average * 2) {
                triggerAlert(new Alert(
                        String.valueOf(ecgRecords.get(i).getPatientId()),
                        "Abnormal ECG peak",
                        ecgRecords.get(i).getTimestamp()
                ));
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
                triggerAlert(new Alert(
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