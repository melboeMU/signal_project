package com.alerts.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.alerts.Alert;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.BloodOxygenAlertFactory;
import com.data_management.PatientRecord;
import com.data_management.RecordLabels;

/**
 * Strategy for detecting abnormal oxygen saturation levels.
 * 
 * @author Melanie Böhmer
 */
public class OxygenSaturationStrategy implements AlertStrategy {

    private static final long TEN_MINUTES_IN_MILLIS = 10 * 60 * 1000;

    private final AlertFactory factory = new BloodOxygenAlertFactory();

    @Override
    /**
     * Evaluates blood oxygen saturation records and generates alerts for abnormal levels < 92 % or rapid drop of 5 % .
     * 
     * @param records the list of patient records to analyze
     * @return a list of generated oxygen saturation alerts
     */
    public List<Alert> checkAlert(List<PatientRecord> records) {
         List<Alert> alerts = new ArrayList<>();

        List<PatientRecord> saturationRecords = getRecordsByType(records, RecordLabels.BLOOD_SATURATION);

        for (PatientRecord record : saturationRecords) {
            if (record.getMeasurementValue() < 92) {
                alerts.add(factory.createAlert(
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
                    alerts.add(factory.createAlert(
                            String.valueOf(later.getPatientId()),
                            "Rapid blood oxygen saturation drop",
                            later.getTimestamp()
                    ));
                    break;
                }
            }
        }

        return alerts;
    }
    /**
     * Filters patient records by record type and sorts them by timestamp.
     *
     * @param records the list of patient records
     * @param recordType the record type to filter by
     * @return a filtered and sorted list of patient records
     */
    private List<PatientRecord> getRecordsByType(List<PatientRecord> records, String recordType) {
        return records.stream()
                .filter(record -> RecordLabels.normalize(record.getRecordType()).equals(recordType))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .collect(Collectors.toList());
    }
}
