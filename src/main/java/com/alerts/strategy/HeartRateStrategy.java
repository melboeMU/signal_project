package com.alerts.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.alerts.Alert;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.ECGAlertFactory;
import com.data_management.PatientRecord;

/**
 * Strategy that monitors for abnormal heart rates. 
 * 
 * @author Melanie Böhmer
 */
public class HeartRateStrategy implements AlertStrategy{

    private static final String ECG = "ECG";
    private static final int SLIDING_WINDOW_SIZE = 5;

    private final AlertFactory factory = new ECGAlertFactory();

    @Override
    /**
     * Evaluates ECG records and generates alerts for abnormal heart rate behavior.
     * 
     * @param records the list of patient records to analyze
     * @return a list of generated heart rate alerts
     */
    public List<Alert> checkAlert(List<PatientRecord> records) {
         List<Alert> alerts = new ArrayList<>();

        List<PatientRecord> ecgRecords = getRecordsByType(records, ECG);

        if (ecgRecords.size() <= SLIDING_WINDOW_SIZE) {
            return alerts;
        }

        for (int i = SLIDING_WINDOW_SIZE; i < ecgRecords.size(); i++) {
            double sum = 0;

            for (int j = i - SLIDING_WINDOW_SIZE; j < i; j++) {
                sum += ecgRecords.get(j).getMeasurementValue();
            }

            double average = sum / SLIDING_WINDOW_SIZE;
            double currentValue = ecgRecords.get(i).getMeasurementValue();

            if (currentValue > average * 2) {
                alerts.add(factory.createAlert(
                        String.valueOf(ecgRecords.get(i).getPatientId()),
                        "Abnormal ECG peak",
                        ecgRecords.get(i).getTimestamp()
                ));
            }
        }
        return alerts;
    }

    /**
     * Filters patient records by record type and sorts them by timestamp.
     *
     * @param records    the list of patient records
     * @param recordType the type of records to filter (e.g., "ECG")
     * @return a filtered and sorted list of patient records
     */
    private List<PatientRecord> getRecordsByType(List<PatientRecord> records, String recordType) {
        return records.stream()
                .filter(record -> record.getRecordType().equalsIgnoreCase(recordType))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .collect(Collectors.toList());
    }
}
