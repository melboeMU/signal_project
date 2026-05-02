package com.alerts.types;

import com.alerts.Alert;

/**
 * Represents an alert Generated for significant changes in blood oxygen levels
 * 
 * @author Melanie Böhmer
 */
public class BloodOxygenAlert extends Alert {
    public BloodOxygenAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
