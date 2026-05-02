package com.alerts.types;

import com.alerts.Alert;

/**
 * Represents an alert for ECG (Electrocardiogram) readings with for irregular heart rates and rhythms
 * 
 * @author Melanie Böhmer
 */
public class ECGAlert extends Alert {
    public ECGAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
