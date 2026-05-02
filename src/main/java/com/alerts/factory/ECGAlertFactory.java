package com.alerts.factory;

import com.alerts.Alert;
import com.alerts.types.ECGAlert;

/**
 * Factory for creating ECG Alerts
 * 
 * @author Melanie Böhmer
 */
public class ECGAlertFactory extends AlertFactory {
    @Override
    /**
     * Creates an ECG Alert with the specified parameters.
     *
     * @param patientId   the ID of the patient
     * @param condition   the condition for the alert
     * @param timestamp   the timestamp of the alert
     * @return the created ECG Alert
     */
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new ECGAlert(patientId, condition, timestamp);
    }
    
}
