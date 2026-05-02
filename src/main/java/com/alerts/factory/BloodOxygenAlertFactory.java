package com.alerts.factory;

import com.alerts.Alert;
import com.alerts.types.BloodOxygenAlert;   

/**
 * Factory for creating Blood Oxygen Alerts
 * 
 * @author Melanie Böhmer
 */
public class BloodOxygenAlertFactory extends AlertFactory {
    @Override
    /**
     * Creates a Blood Oxygen Alert with the specified parameters.
     *
     * @param patientId   the ID of the patient
     * @param condition   the condition for the alert
     * @param timestamp   the timestamp of the alert
     * @return the created Blood Oxygen Alert
     */
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodOxygenAlert(patientId, condition, timestamp);
    }
    
}
