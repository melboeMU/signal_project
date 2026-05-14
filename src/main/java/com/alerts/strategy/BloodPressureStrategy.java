package com.alerts.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.alerts.Alert;
import com.data_management.PatientRecord;
import com.data_management.RecordLabels;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.BloodPressureAlertFactory;

/**
 * Strategy for detecting abnormal blood pressure conditions
 * 
 * @author Melanie Böhmer
 */
public class BloodPressureStrategy implements AlertStrategy {

    private final AlertFactory factory = new BloodPressureAlertFactory();

    @Override
    /**
     * Checks for abnormal blood pressure conditions in the provided patient records.looking at trends and threshold violations.
     * 
     * @param records the list of patient records to analyze
     * @return a list of generated blood pressure alerts
     */
    public List<Alert> checkAlert(List<PatientRecord> records) {

        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> systolicRecords = getRecordsByType(records, RecordLabels.SYSTOLIC_BLOOD_PRESSURE);
        List<PatientRecord> diastolicRecords = getRecordsByType(records, RecordLabels.DIASTOLIC_BLOOD_PRESSURE);

        alerts.addAll(evaluateBloodPressureThresholds(systolicRecords, true));
        alerts.addAll(evaluateBloodPressureThresholds(diastolicRecords, false));

        alerts.addAll(evaluateBloodPressureTrend(
                systolicRecords,
                "Increasing systolic blood pressure trend",
                "Decreasing systolic blood pressure trend"
        ));

        alerts.addAll(evaluateBloodPressureTrend(
                diastolicRecords,
                "Increasing diastolic blood pressure trend",
                "Decreasing diastolic blood pressure trend"
        ));

        return alerts;
    }

    /**
     * Evaluates blood pressure threshold violations.
     *
     * @param records   the records to analyze
     * @param systolic  true if systolic, false if diastolic
     * @return list of generated alerts
     */
    private List<Alert> evaluateBloodPressureThresholds(List<PatientRecord> records, boolean systolic) {
        List<Alert> alerts = new ArrayList<>();

        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();

            boolean thresholdExceeded;

            if (systolic) {
                thresholdExceeded = value > 180 || value < 90;
            } else {
                thresholdExceeded = value > 120 || value < 60;
            }

            if (thresholdExceeded) {
                alerts.add(factory.createAlert(
                        String.valueOf(record.getPatientId()),
                        "Critical blood pressure threshold",
                        record.getTimestamp()
                ));
            }
        }

        return alerts;
    }

    /**
     * Evaluates trends in blood pressure values.
     *
     * @param records             the records to analyze
     * @param increasingCondition description for increasing trend
     * @param decreasingCondition description for decreasing trend
     * @return list of generated alerts
     */
    private List<Alert> evaluateBloodPressureTrend(
            List<PatientRecord> records,
            String increasingCondition,
            String decreasingCondition
    ) {
        List<Alert> alerts = new ArrayList<>();

        if (records.size() < 3) {
            return alerts;
        }

        for (int i = 0; i <= records.size() - 3; i++) {
            PatientRecord first = records.get(i);
            PatientRecord second = records.get(i + 1);
            PatientRecord third = records.get(i + 2);

            double firstChange = second.getMeasurementValue() - first.getMeasurementValue();
            double secondChange = third.getMeasurementValue() - second.getMeasurementValue();

            if (firstChange > 10 && secondChange > 10) {
                alerts.add(factory.createAlert(
                        String.valueOf(third.getPatientId()),
                        increasingCondition,
                        third.getTimestamp()
                ));
            }

            if (firstChange < -10 && secondChange < -10) {
                alerts.add(factory.createAlert(
                        String.valueOf(third.getPatientId()),
                        decreasingCondition,
                        third.getTimestamp()
                ));
            }
        }

        return alerts;
    }

    /**
     * Filters records by type and sorts them by timestamp.
     *
     * @param records    the list of records
     * @param recordType the type to filter
     * @return filtered and sorted records
     */
    private List<PatientRecord> getRecordsByType(List<PatientRecord> records, String recordType) {
        return records.stream()
                .filter(record -> RecordLabels.normalize(record.getRecordType()).equals(recordType))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .collect(Collectors.toList());
    }    
}
