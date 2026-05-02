package com.alerts.types;

import com.alerts.Alert;

/**
 * Represents an alert for Generated for blood pressure anomalies
 * 
 * @author Melanie Böhmer
 */
public class BloodPressureAlert extends Alert {
    public BloodPressureAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
